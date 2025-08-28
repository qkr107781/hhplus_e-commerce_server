package kr.hhplus.be.server.application.payment.event.publisher;

import kr.hhplus.be.server.application.payment.dto.PaymentResponse;
import kr.hhplus.be.server.application.product.dto.ProductResponse;

import java.util.List;

public class PaymentCreateEventPublisher {

    public record SendDataPlatform (
            PaymentResponse.Create response
    ){}
    public record SendRedis (
            List<ProductResponse.StatisticsRedis> redisDataList
    ){}

}
