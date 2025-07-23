package kr.hhplus.be.server.application.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public class ProductRequest {

    public record Option(
            @Schema(description = "상품 옵션 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            long productOptionId
    ) {
    }

    public record Select(
            @Schema(description = "상품 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            long productId,
            @Schema(description = "상품 옵션 목록", requiredMode = Schema.RequiredMode.REQUIRED)
            List<ProductRequest.Option> options
    ) {
        public static ProductRequest.Select from(ProductRequest.Select product) {
            return new ProductRequest.Select(
                    product.productId(),
                    product.options());
        }
    }
}