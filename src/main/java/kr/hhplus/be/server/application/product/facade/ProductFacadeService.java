package kr.hhplus.be.server.application.product.facade;

import kr.hhplus.be.server.application.order.dto.OrderProductSummary;
import kr.hhplus.be.server.application.order.service.OrderProductService;
import kr.hhplus.be.server.application.order.service.OrderService;
import kr.hhplus.be.server.application.product.dto.ProductResponse;
import kr.hhplus.be.server.application.product.service.ProductService;
import kr.hhplus.be.server.application.product.service.ProductStatisticsService;
import kr.hhplus.be.server.application.product.service.ProductStatisticsUseCase;
import kr.hhplus.be.server.common.redis.RedisKeys;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderProduct;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductOption;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.redisson.api.SetUnionArgs;
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
    private static final String DAILY_SALES_PREFIX = RedisKeys.DAILY_SALES_PREFIX.format();
    private final String PRODUCT_STATISTICS_CACHE_KEY = "'top5:sales:last3days'";
    private final String PRODUCT_STATISTICS_CACHE_NAME = "topSalesProducts";

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
     *
     * @return 상위 5개 통계 데이터 리스트
     */
    @Cacheable(
            cacheNames = PRODUCT_STATISTICS_CACHE_NAME,
            key = PRODUCT_STATISTICS_CACHE_KEY,
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
        for (Order order : orderList) {
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
    @Cacheable(
            cacheNames = PRODUCT_STATISTICS_CACHE_NAME,
            key = PRODUCT_STATISTICS_CACHE_KEY,
            unless = "#result == null  || #result.isEmpty()"
    )
    @Override
    public List<ProductResponse.Statistics> getTop5ForLast3Days() {
        // 1. 지난 3일간 ZSET 키 생성
        String[] last3DaysKeys = new String[3];
        for (int i = 0; i < 3; i++) {
            last3DaysKeys[i] = DAILY_SALES_PREFIX + LocalDate.now().minusDays(i + 1).format(DATE_FORMATTER);
        }

        // 2. 임시 ZSET 키 생성
        RScoredSortedSet<Long> unionSet = redissonClient.getScoredSortedSet(last3DaysKeys[0], new LongCodec());

        // 3. SetUnionArgs → Redisson 3.50에서는 SetReadArgs로 받기
        SetUnionArgs unionArgs = SetUnionArgs.names(last3DaysKeys);

        // 4. Redis 서버에서 union 수행
        unionSet.readUnion(unionArgs);

        // 5. Top5 추출 (점수 내림차순)
        Collection<Long> top5Ids = unionSet.valueRangeReversed(0, 4);

        // 6. 상품명 + 판매량 변환
        List<ProductResponse.Statistics> resultList = new ArrayList<>();
        for (Long productOptionId : top5Ids) {
            Double score = unionSet.getScore(productOptionId);
            if (score == null) continue;

            ProductOption productOption = productService.selectProductOptionByProductOptionId(productOptionId);
            Product product = productService.selectProductByProductId(productOption.getProductId());

            resultList.add(new ProductResponse.Statistics(
                    product.getName() + "-" + productOption.getOptionName(),
                    score.longValue()
            ));
        }

        return resultList;
    }
}
