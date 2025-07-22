package kr.hhplus.be.server.domain.product;

import java.time.LocalDateTime;

public class ProductStatistics {

    private Long statisticsId;
    private Long productId;
    private Long productOptionId;
    private String productName;
    private String productOptionName;
    private Long price;
    private Long salesQuantity;
    private Long ranking;
    private LocalDateTime selectionDate;

    public ProductStatistics(Long statisticsId, Long productId, Long productOptionId, String productName, String productOptionName, Long price, Long salesQuantity, Long ranking, LocalDateTime selectionDate) {
        this.statisticsId = statisticsId;
        this.productId = productId;
        this.productOptionId = productOptionId;
        this.productName = productName;
        this.productOptionName = productOptionName;
        this.price = price;
        this.salesQuantity = salesQuantity;
        this.ranking = ranking;
        this.selectionDate = selectionDate;
    }

    public Long getStatisticsId() {
        return statisticsId;
    }

    public Long getProductId() {
        return productId;
    }

    public Long getProductOptionId() {
        return productOptionId;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductOptionName() {
        return productOptionName;
    }

    public Long getPrice() {
        return price;
    }

    public Long getSalesQuantity() {
        return salesQuantity;
    }

    public Long getRanking() {
        return ranking;
    }

    public LocalDateTime getSelectionDate() {
        return selectionDate;
    }
}