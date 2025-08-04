package kr.hhplus.be.server.application.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderProduct;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductOption;

import java.time.LocalDateTime;
import java.util.List;

public class OrderResponse {

    public record OrderCreateProduct(
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
            @Schema(description = "상품 주문 수량", requiredMode = Schema.RequiredMode.REQUIRED)
            long productQuantity,
            @Schema(description = "상품 개당 가격", requiredMode = Schema.RequiredMode.REQUIRED)
            long productPrice
    ){
        public static OrderResponse.OrderCreateProduct from(OrderProduct orderProduct, Product product, ProductOption productOption){
            return new OrderResponse.OrderCreateProduct(orderProduct.getOrderProductId(),
                                                        orderProduct.getProductId(),
                                                        product.getName(),
                                                        orderProduct.getProductOptionId(),
                                                        productOption.getOptionName(),
                                                        orderProduct.getProductQuantity(),
                                                        orderProduct.getProductPrice());
        }
    }

    public record OrderCreate(
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
            List<OrderResponse.OrderCreateProduct> orderProduct

    ){
        public static OrderResponse.OrderCreate from(Order order, Coupon coupon, List<OrderResponse.OrderCreateProduct> orderProductList){
            return new OrderResponse.OrderCreate(order.getOrderId(),
                                                order.getCouponId(),
                                                coupon.getCouponName(),
                                                order.getCouponDiscountPrice(),
                                                order.getTotalPrice(),
                                                order.getOrderStatus(),
                                                order.getOrderDate(),
                                                orderProductList);
        }
    }
}