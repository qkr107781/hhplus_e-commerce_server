package kr.hhplus.be.server.application.product.service;

import kr.hhplus.be.server.application.order.dto.OrderProductSummary;
import kr.hhplus.be.server.application.product.dto.ProductResponse;
import kr.hhplus.be.server.application.product.repository.ProductStatisticsRepository;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class ProductStatisticsService {

    private final ProductStatisticsRepository productStatisticsRepository;

    private final RedissonClient redissonClient;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final String DAILY_SALES_PREFIX = "daily:sales:";

    public ProductStatisticsService(ProductStatisticsRepository productStatisticsRepository, RedissonClient redissonClient) {
        this.productStatisticsRepository = productStatisticsRepository;
        this.redissonClient = redissonClient;
    }

    public List<ProductResponse.Statistics> selectTop5SalesProductBySpecificRange(List<OrderProductSummary> orderProductList) {
        return ProductResponse.Statistics.from(productStatisticsRepository.selectTop5SalseProductByLast3Days(orderProductList));
    }

    /**
     * 결제 완료된 상품의 일별 판매량을 업데이트
     *
     * @param productOptionId 판매된 상품옵션 ID
     * @param quantity  판매된 수량
     */
    public void updateDailyRanking(long productOptionId, long quantity) {
        // 오늘 날짜를 기반으로 키를 생성 (예: daily:sales:20250821)
        String todayKey = DAILY_SALES_PREFIX + LocalDate.now().format(DATE_FORMATTER);

        RScoredSortedSet<Long> dailySales = redissonClient.getScoredSortedSet(todayKey);

        dailySales.addScore(productOptionId, quantity);
        dailySales.expire(Instant.now().plus((86400 * 3) + 3600, ChronoUnit.SECONDS));//TTL: 3일 + 1시간 보관 후 삭제
    }
}
