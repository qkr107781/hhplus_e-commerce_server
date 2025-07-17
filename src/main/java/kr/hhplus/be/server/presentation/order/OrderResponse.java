package kr.hhplus.be.server.presentation.order;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

public class OrderResponse {

    public record OrderProduct(
        @Schema(description = "주문 상품 ID", requiredMode = Schema.RequiredMode.REQUIRED)
        long order_product_id,
        @Schema(description = "주문 ID", requiredMode = Schema.RequiredMode.REQUIRED)
        long product_id,
        @Schema(description = "상품명", requiredMode = Schema.RequiredMode.REQUIRED)
        String product_name,
        @Schema(description = "상품 옵션 ID", requiredMode = Schema.RequiredMode.REQUIRED)
        long product_option_id,
        @Schema(description = "상품 옵션명", requiredMode = Schema.RequiredMode.REQUIRED)
        String product_option_name,
        @Schema(description = "상품 잔여 수량", requiredMode = Schema.RequiredMode.REQUIRED)
        long product_quantity,
        @Schema(description = "상품 개당 가격", requiredMode = Schema.RequiredMode.REQUIRED)
        long product_price
    ){
    }

    public record Create(
            @Schema(description = "주문 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            long order_id,
            @Schema(description = "쿠폰 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            long coupon_id,
            @Schema(description = "쿠폰명", requiredMode = Schema.RequiredMode.REQUIRED)
            String coupon_name,
            @Schema(description = "할인금액", requiredMode = Schema.RequiredMode.REQUIRED)
            long coupon_discount_price,
            @Schema(description = "총 주문 금액", requiredMode = Schema.RequiredMode.REQUIRED)
            long total_price,
            @Schema(description = "주문 상태", requiredMode = Schema.RequiredMode.REQUIRED)
            String order_status,
            @Schema(description = "주문일", requiredMode = Schema.RequiredMode.REQUIRED)
            LocalDateTime order_date,
            @Schema(description = "주문 상품", requiredMode = Schema.RequiredMode.REQUIRED)
            List<OrderProduct> order_product

    ){
        public static OrderResponse.Create from(OrderResponse.Create create){
            return new OrderResponse.Create(create.order_id,
                    create.coupon_id,
                    create.coupon_name,
                    create.coupon_discount_price,
                    create.total_price,
                    create.order_status,
                    create.order_date,
                    create.order_product);
        }
    }
}
