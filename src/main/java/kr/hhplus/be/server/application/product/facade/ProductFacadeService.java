package kr.hhplus.be.server.application.product.facade;

import kr.hhplus.be.server.application.order.dto.OrderProductSummary;
import kr.hhplus.be.server.application.order.service.OrderProductService;
import kr.hhplus.be.server.application.order.service.OrderService;
import kr.hhplus.be.server.application.product.dto.ProductResponse;
import kr.hhplus.be.server.application.product.service.ProductService;
import kr.hhplus.be.server.application.product.service.ProductStatisticsService;
import kr.hhplus.be.server.application.product.service.ProductStatisticsUseCase;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderProduct;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductOption;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.LongCodec;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ProductFacadeService implements ProductStatisticsUseCase {

    private final OrderService orderService;
    private final OrderProductService orderProductService;
    private final ProductStatisticsService productStatisticsService;
    private final ProductService productService;
    private final RedissonClient redissonClient;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final String DAILY_SALES_PREFIX = "daily:sales:";
    private static final String CACHE_NAME = "topSalesProducts";
    private static final String CACHE_KEY = "'top5:sales:last3days'";

    public ProductFacadeService(OrderService orderService, OrderProductService orderProductService, ProductStatisticsService productStatisticsService, ProductService productService, RedissonClient redissonClient) {
        this.orderService = orderService;
        this.orderProductService = orderProductService;
        this.productStatisticsService = productStatisticsService;
        this.productService = productService;
        this.redissonClient = redissonClient;
    }

    /**
     * 오늘 기준 4일 전부터 1일 전까지의 데이터 중 salesQuantity 기준 상위 5개를 조회합니다.
     * 예: 오늘이 7월 25일이면, 7월 21일 부터 7월 24일 까지의 데이터를 조회합니다.
     * @return 상위 5개 통계 데이터 리스트
     */
    @Cacheable(
            cacheNames = CACHE_NAME,
            key = CACHE_KEY,
            unless = "#result == null  || #result.isEmpty()"
    )
    @Transactional(readOnly = true)
    @Override
    public List<ProductResponse.Statistics> selectTop5SalesProductBySpecificRange() {
        List<OrderProduct> orderProductListByBefore3Days = new ArrayList<>();

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = LocalDate.now().minusDays(3);

        List<Order> orderList = orderService.selectOrderByOrderStatusAndOrderDateBetween("complete_payment", startDate, endDate);

        //추출일 기준 3일전~1일전 결제 완료된 주문의 주문 상품 조회
        for (Order order : orderList){
            List<OrderProduct> orderProductList = orderProductService.selectOrderProductsByOrderIdOrderByProductOptionIdAsc(order.getOrderId());
            orderProductListByBefore3Days.addAll(orderProductList);
        }

        List<OrderProductSummary> top5OrderProductList = orderProductService.getTop5OrderProduct(orderProductListByBefore3Days);

        return productStatisticsService.selectTop5SalesProductBySpecificRange(top5OrderProductList);
    }

    /**
     * 지난 3일간 판매 TOP 5 순위를 계산
     *
     * @return 합산된 판매량 순위 목록 (점수 포함)
     */
    @Override
    public List<ProductResponse.Statistics> getTop5ForLast3Days() {
        Map<Long, Double> totalScores = new HashMap<>();

        String[] last3DaysKeys = new String[3];
        for (int i = 0; i < 3; i++) {
            last3DaysKeys[i] = DAILY_SALES_PREFIX + LocalDate.now().minusDays(i+1).format(DATE_FORMATTER);
        }

        // 1. 각 날짜별 Sorted Sets 에서 상위 5개 데이터를 읽어와 합산
        for (String key : last3DaysKeys) {
            RScoredSortedSet<Long> dailySales = redissonClient.getScoredSortedSet(key, new LongCodec());
            for (Long productId : dailySales.valueRangeReversed(0, 4)) {
                Double score = dailySales.getScore(productId);
                if (score != null) {
                    totalScores.merge(productId, score, Double::sum);
                }
            }
        }

        // 2. 합산된 맵을 스트림으로 변환하고, 점수를 기준으로 내림차순 정렬하여 상위 5개를 추출
        List<ProductResponse.StatisticsRedis> redisStatisticsList = totalScores.entrySet().stream()
                                                                                        .map(entry -> new ProductResponse.StatisticsRedis(entry.getKey(), entry.getValue()))
                                                                                        .sorted(Comparator.comparingDouble(ProductResponse.StatisticsRedis::score).reversed())
                                                                                        .limit(5)
                                                                                        .toList();

        // 3. 상품명, 수량 형식으로 변경하여 리턴
        List<ProductResponse.Statistics> resultList = new ArrayList<>();
        for(ProductResponse.StatisticsRedis redisStatistics : redisStatisticsList){
            ProductOption productOption = productService.selectProductOptionByProductOptionId(redisStatistics.productOptionId());
            Product product = productService.selectProductByProductId(productOption.getProductId());
            String productName = product.getName() + "-" + productOption.getOptionName();
            long salesQuantity = (long)redisStatistics.score();
            ProductResponse.Statistics statistics = new ProductResponse.Statistics(productName,salesQuantity);
            resultList.add(statistics);
        }

        return resultList;
    }
}
