package kr.hhplus.be.server.domain.product;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "product_statistics")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductStatistics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "statistics_id")
    private Long statisticsId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "product_name", length = 50, nullable = false)
    private String productName;

    @Column(name = "sales_quantity", nullable = false)
    private Long salesQuantity;

    @Column(name = "selection_date", nullable = false)
    private LocalDate selectionDate;

    @Builder
    public ProductStatistics(Long statisticsId, Long productId, String productName, Long salesQuantity, LocalDate selectionDate) {
        this.statisticsId = statisticsId;
        this.productId = productId;
        this.productName = productName;
        this.salesQuantity = salesQuantity;
        this.selectionDate = selectionDate;
    }
}