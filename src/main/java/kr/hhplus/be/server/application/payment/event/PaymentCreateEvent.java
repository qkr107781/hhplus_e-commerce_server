package kr.hhplus.be.server.application.payment.event;

import kr.hhplus.be.server.application.payment.dto.PaymentResponse;
import kr.hhplus.be.server.application.product.dto.ProductResponse;

import java.util.List;

public class PaymentCreateEvent{

    public record SendDataPlatform (
            PaymentResponse.Create response
    ){}
    public record SendRedis (
            List<ProductResponse.StatisticsRedis> redisDataList
    ){}

}
