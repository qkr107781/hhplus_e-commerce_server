package kr.hhplus.be.server.persistence.product;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "product_statistics")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductStatisticsJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "statistics_id")
    private Long statisticsId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "product_option_id", nullable = false)
    private Long productOptionId;

    @Column(name = "product_name", length = 50, nullable = false)
    private String productName;

    @Column(name = "product_option_name", length = 50, nullable = false)
    private String productOptionName;

    @Column(name = "price", nullable = false)
    private Long price;

    @Column(name = "sales_quantity", nullable = false)
    private Long salesQuantity;

    @Column(name = "ranking", nullable = false)
    private Long ranking;

    @Column(name = "selection_date", nullable = false)
    private LocalDateTime selectionDate;

    @Builder
    public ProductStatisticsJpaEntity(Long statisticsId, Long productId, Long productOptionId, String productName,
                                      String productOptionName, Long price, Long salesQuantity,
                                      Long ranking, LocalDateTime selectionDate) {
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
}