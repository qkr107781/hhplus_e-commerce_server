package kr.hhplus.be.server.application.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class PaymentRequest {
    public record Create(
            @Schema(description = "사용자 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            long userId,
            @Schema(description = "주문 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            long orderId
    ){
    }
}