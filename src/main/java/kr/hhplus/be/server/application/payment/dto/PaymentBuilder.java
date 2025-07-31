package kr.hhplus.be.server.application.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public class PaymentBuilder {
    public record Payment(
            @Schema(description = "사용자 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            long userId,
            @Schema(description = "주문 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            long orderId,
            @Schema(description = "결제 금액", requiredMode = Schema.RequiredMode.REQUIRED)
            long paymentPrice,
            @Schema(description = "결제일", requiredMode = Schema.RequiredMode.REQUIRED)
            LocalDateTime paymentDate
    ){
        public static kr.hhplus.be.server.domain.payment.Payment toDomain(PaymentBuilder.Payment requestPayment){
            return kr.hhplus.be.server.domain.payment.Payment.builder()
                    .userId(requestPayment.userId())
                    .orderId(requestPayment.orderId())
                    .paymentPrice(requestPayment.paymentPrice())
                    .paymentDate(requestPayment.paymentDate())
                    .build();
        }
    }
}
