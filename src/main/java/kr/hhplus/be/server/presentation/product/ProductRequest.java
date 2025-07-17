package kr.hhplus.be.server.presentation.product;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public class ProductRequest {

    public record Option(
            @Schema(description = "상품 옵션 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            long product_option_id
    ) {
    }

    public record Select(
            @Schema(description = "상품 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            long product_id,
            @Schema(description = "상품 옵션 목록", requiredMode = Schema.RequiredMode.REQUIRED)
            List<ProductRequest.Option> options
    ) {
        public static ProductRequest.Select from(ProductRequest.Select product) {
            return new ProductRequest.Select(
                    product.product_id(),
                    product.options());
        }
    }
}
