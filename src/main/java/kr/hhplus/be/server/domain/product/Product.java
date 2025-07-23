package kr.hhplus.be.server.domain.product;

import java.util.ArrayList;
import java.util.List;

public class Product {

    private Long productId;
    private String name;
    private String description;
    private List<ProductOption> productOptions;

    public Product(Long productId, String name, String description, List<ProductOption> productOptions) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.productOptions = productOptions;
    }

    public Long getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<ProductOption> getProductOptions() {
        return productOptions;
    }
}