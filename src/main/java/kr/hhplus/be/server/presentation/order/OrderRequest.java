package kr.hhplus.be.server.presentation.order;

import kr.hhplus.be.server.presentation.product.ProductRequest;

import java.util.List;

public class OrderRequest {
    public record Create(
            long user_id,
            List<ProductRequest> product_ids,
            long coupon_id
    ){
    }
}
