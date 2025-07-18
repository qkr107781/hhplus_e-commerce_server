package kr.hhplus.be.server.presentation.payment;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.presentation.order.OrderResponse;

public class PaymentResponse {
    public record Create(
            @Schema(description = "결제 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            long paymentId,
            @Schema(description = "주문 상품 정보", requiredMode = Schema.RequiredMode.REQUIRED)
            OrderResponse.Create order
    ){
        public static PaymentResponse.Create from(PaymentResponse.Create create){
            return new PaymentResponse.Create(create.paymentId, create.order);
        }
    }
}