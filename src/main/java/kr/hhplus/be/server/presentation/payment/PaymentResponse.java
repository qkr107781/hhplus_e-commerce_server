package kr.hhplus.be.server.presentation.payment;

import kr.hhplus.be.server.presentation.order.OrderResponse;

public class PaymentResponse {
    public record Create(
        long payment_id,
        OrderResponse.Create order
    ){
        public static PaymentResponse.Create from(PaymentResponse.Create create){
            return new PaymentResponse.Create(create.payment_id,create.order);
        }
    }
}
