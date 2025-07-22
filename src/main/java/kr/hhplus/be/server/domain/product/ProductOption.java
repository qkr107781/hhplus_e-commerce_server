package kr.hhplus.be.server.domain.product;

import java.time.LocalDateTime;

public class ProductOption {

    private Long productOptionId;
    private Long productId;
    private String optionName;
    private Long price;
    private Long totalQuantity;
    private Long stockQuantity;
    private String salesYn;
    private LocalDateTime regDate;

    public ProductOption(Long productOptionId, Long productId, String optionName, Long price, Long totalQuantity, Long stockQuantity, String salesYn, LocalDateTime regDate) {
        this.productOptionId = productOptionId;
        this.productId = productId;
        this.optionName = optionName;
        this.price = price;
        this.totalQuantity = totalQuantity;
        this.stockQuantity = stockQuantity;
        this.salesYn = salesYn;
        this.regDate = regDate;
    }

    public Long getProductOptionId() {
        return productOptionId;
    }

    public Long getProductId() {
        return productId;
    }

    public String getOptionName() {
        return optionName;
    }

    public Long getPrice() {
        return price;
    }

    public Long getTotalQuantity() {
        return totalQuantity;
    }

    public Long getStockQuantity() {
        return stockQuantity;
    }

    public String getSalesYn() {
        return salesYn;
    }

    public LocalDateTime getRegDate() {
        return regDate;
    }
}