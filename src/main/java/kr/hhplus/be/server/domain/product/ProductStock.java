package kr.hhplus.be.server.domain.product;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_stock")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_stock_id")
    private Long productStockId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_option_id", nullable = false)
    private ProductOption productOption;

    @Column(name = "total_quantity", nullable = false)
    private Long totalQuantity;

    @Column(name = "stock_quantity", nullable = false)
    private Long stockQuantity;

    @Builder
    public ProductStock(Long productStockId, ProductOption productOption, Long totalQuantity, Long stockQuantity) {
        this.productStockId = productStockId;
        this.productOption = productOption;
        this.totalQuantity = totalQuantity;
        this.stockQuantity = stockQuantity;
    }

    /**
     * 재고 차감
     * @throws Exception
     */
    public void decreaseProductQuantity() throws Exception {
        if(stockQuantity == 0){
            throw new Exception("stock empty");
        }
        this.stockQuantity = stockQuantity - 1;
    }
}
