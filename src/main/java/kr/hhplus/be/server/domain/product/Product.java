package kr.hhplus.be.server.domain.product;

import java.util.ArrayList;
import java.util.List;

public class Product {

    private Long productId;
    private String name;
    private String description;
    private List<ProductOption> productOptions;

    public Product(Long productId, String name, String description, List<ProductOption> productOptions) {
        //상품 판매 여부 체크
        List<ProductOption> salesProductOptionList = new ArrayList<>();
        if(!productOptions.isEmpty()){
            for (ProductOption productOption : productOptions) {
                if ("Y".equalsIgnoreCase(productOption.getSalesYn())) {
                    salesProductOptionList.add(productOption);
                }
            }
        }

        //상품 옵션 중 1개라도 판매 중 일때만 할당
        if(!salesProductOptionList.isEmpty()){
            this.productId = productId;
            this.name = name;
            this.description = description;
            this.productOptions = salesProductOptionList;
        }
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