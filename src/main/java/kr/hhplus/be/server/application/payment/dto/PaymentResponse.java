package kr.hhplus.be.server.application.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.application.order.dto.OrderResponse;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.payment.Payment;

import java.time.LocalDateTime;
import java.util.List;

public class PaymentResponse {
    public record Create(
            @Schema(description = "결제 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            long paymentId,
            @Schema(description = "결제 금액", requiredMode = Schema.RequiredMode.REQUIRED)
            long paymentPrice,
            @Schema(description = "결제일", requiredMode = Schema.RequiredMode.REQUIRED)
            LocalDateTime paymentDate,
            @Schema(description = "주문 상품 정보", requiredMode = Schema.RequiredMode.REQUIRED)
            OrderResponse.OrderCreate order
    ){
        public static PaymentResponse.Create from(Payment payment, Order order, Coupon coupon, List<OrderResponse.OrderCreateProduct> orderProductList){
            return new PaymentResponse.Create(payment.getPaymentId(),payment.getPaymentPrice(),payment.getPaymentDate(),
                    OrderResponse.OrderCreate.from(order,coupon, orderProductList));
        }
    }
}