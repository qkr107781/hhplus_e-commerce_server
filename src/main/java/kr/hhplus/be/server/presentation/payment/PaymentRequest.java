package kr.hhplus.be.server.presentation.payment;

public class PaymentRequest {
    public record Create(
        long user_id,
        long order_id
    ){
    }
}
