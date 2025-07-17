package kr.hhplus.be.server.presentation.order;

import java.time.LocalDateTime;
import java.util.List;

public class OrderResponse {

    public record OrderProduct(
        long order_product_id,
        long product_id,
        String product_name,
        long product_option_id,
        String product_option_name,
        long product_quantity,
        long product_price
    ){
    }

    public record Create(
            long order_id,
            long coupon_id,
            String coupon_name,
            long coupon_discount_price,
            long total_price,
            String order_status,
            LocalDateTime order_date,
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
