package kr.hhplus.be.server.presentation.order;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

public class OrderResponse {

    public record OrderProduct(
            @Schema(description = "주문 상품 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            long orderProductId,
            @Schema(description = "주문 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            long productId,
            @Schema(description = "상품명", requiredMode = Schema.RequiredMode.REQUIRED)
            String productName,
            @Schema(description = "상품 옵션 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            long productOptionId,
            @Schema(description = "상품 옵션명", requiredMode = Schema.RequiredMode.REQUIRED)
            String productOptionName,
            @Schema(description = "상품 잔여 수량", requiredMode = Schema.RequiredMode.REQUIRED)
            long productQuantity,
            @Schema(description = "상품 개당 가격", requiredMode = Schema.RequiredMode.REQUIRED)
            long productPrice
    ){
    }

    public record Create(
            @Schema(description = "주문 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            long orderId,
            @Schema(description = "쿠폰 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            long couponId,
            @Schema(description = "쿠폰명", requiredMode = Schema.RequiredMode.REQUIRED)
            String couponName,
            @Schema(description = "할인금액", requiredMode = Schema.RequiredMode.REQUIRED)
            long couponDiscountPrice,
            @Schema(description = "총 주문 금액", requiredMode = Schema.RequiredMode.REQUIRED)
            long totalPrice,
            @Schema(description = "주문 상태", requiredMode = Schema.RequiredMode.REQUIRED)
            String orderStatus,
            @Schema(description = "주문일", requiredMode = Schema.RequiredMode.REQUIRED)
            LocalDateTime orderDate,
            @Schema(description = "주문 상품", requiredMode = Schema.RequiredMode.REQUIRED)
            List<OrderProduct> orderProduct

    ){
        public static OrderResponse.Create from(OrderResponse.Create create){
            return new OrderResponse.Create(create.orderId,
                    create.couponId,
                    create.couponName,
                    create.couponDiscountPrice,
                    create.totalPrice,
                    create.orderStatus,
                    create.orderDate,
                    create.orderProduct);
        }
    }
}