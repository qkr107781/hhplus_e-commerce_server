package kr.hhplus.be.server.persistence.external.redis;

import kr.hhplus.be.server.application.product.dto.ProductResponse;
import kr.hhplus.be.server.application.product.service.ProductStatisticsService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AsyncRedisSender {

    private final ProductStatisticsService productStatisticsService;

    public AsyncRedisSender(ProductStatisticsService productStatisticsService) {
        this.productStatisticsService = productStatisticsService;
    }

    public void sendToRedisTop5ProductStatisticsData(List<ProductResponse.StatisticsRedis> redisDataList){
        for (ProductResponse.StatisticsRedis redisData : redisDataList){
            //레디스 인기상품 데이터 Sorted Sets 입력을 위한 전송
            productStatisticsService.updateDailyRanking(redisData.productOptionId(),(long)redisData.score());
        }
    }
}
