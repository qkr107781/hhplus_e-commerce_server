package kr.hhplus.be.server.presentation.order;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.presentation.product.ProductRequest;

import java.util.List;

public class OrderRequest {
    public record Create(
            @Schema(description = "사용자 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            long userId,
            @Schema(description = "주문 상품 ID 목록", requiredMode = Schema.RequiredMode.REQUIRED)
            List<ProductRequest> productIds,
            @Schema(description = "쿠폰 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            long couponId
    ){
    }
}