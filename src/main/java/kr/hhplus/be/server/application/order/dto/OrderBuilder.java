package kr.hhplus.be.server.application.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public class OrderBuilder {
    public record Order(
            @Schema(description = "사용자 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            long userId,
            @Schema(description = "쿠폰 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            long couponId,
            @Schema(description = "쿠폰 할인 금액", requiredMode = Schema.RequiredMode.REQUIRED)
            long couponDiscountPrice,
            @Schema(description = "총 주문 금액", requiredMode = Schema.RequiredMode.REQUIRED)
            long totalPrice,
            @Schema(description = "주문 상태 값", requiredMode = Schema.RequiredMode.REQUIRED)
            String orderStatus,
            @Schema(description = "주문일", requiredMode = Schema.RequiredMode.REQUIRED)
            LocalDateTime orderDate
    ){
        public static kr.hhplus.be.server.domain.order.Order toDomain(OrderBuilder.Order requestOrder){
            return kr.hhplus.be.server.domain.order.Order.builder()
                    .userId(requestOrder.userId())
                    .couponId(requestOrder.couponId())
                    .couponDiscountPrice(requestOrder.couponDiscountPrice())
                    .totalPrice(requestOrder.totalPrice())
                    .orderStatus(requestOrder.orderStatus())
                    .orderDate(requestOrder.orderDate())
                    .build();
        }
    }
    public record OrderProduct(
            @Schema(description = "주문 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            long orderId,
            @Schema(description = "상품 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            long productId,
            @Schema(description = "상품 옵션 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            long productOptionId,
            @Schema(description = "주문 상품 수량", requiredMode = Schema.RequiredMode.REQUIRED)
            long productQuantity,
            @Schema(description = "상품 가격", requiredMode = Schema.RequiredMode.REQUIRED)
            long productPrice
    ){
        public static kr.hhplus.be.server.domain.order.OrderProduct toDomain(OrderBuilder.OrderProduct requestOrderProduct){
            return kr.hhplus.be.server.domain.order.OrderProduct.builder()
                    .orderId(requestOrderProduct.orderId())
                    .productId(requestOrderProduct.productId())
                    .productOptionId(requestOrderProduct.productOptionId())
                    .productQuantity(requestOrderProduct.productQuantity())
                    .productPrice(requestOrderProduct.productPrice())
                    .build();
        }
    }
}
