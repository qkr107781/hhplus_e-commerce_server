package kr.hhplus.be.server.presentation.product;

import java.util.List;

public class ProductRequest {

    public record Option(
            long product_option_id
    ) {
    }

    public record Select(
            long product_id,
            List<ProductRequest.Option> options
    ) {
        public static ProductRequest.Select from(ProductRequest.Select product) {
            return new ProductRequest.Select(
                    product.product_id(),
                    product.options());
        }
    }
}
