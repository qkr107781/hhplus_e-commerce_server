package kr.hhplus.be.server.domain.product;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.order.Order;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "product_option")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_option_id")
    private Long productOptionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="product_id", nullable = false)
    private Product product;

    @Column(name = "option_name", length = 50, nullable = false)
    private String optionName;

    @Column(name = "price", nullable = false)
    private Long price;

    @Column(name = "total_quantity", nullable = false)
    private Long totalQuantity;

    @Column(name = "stock_quantity", nullable = false)
    private Long stockQuantity;

    @Column(name = "sales_yn", length = 1, nullable = false)
    private String salesYn;

    @Column(name = "reg_date", nullable = false)
    private LocalDateTime regDate;

    @Builder
    public ProductOption(Long productOptionId, Product product, String optionName, Long price, Long totalQuantity,
                         Long stockQuantity, String salesYn, LocalDateTime regDate) {
        this.productOptionId = productOptionId;
        this.product = product;
        this.optionName = optionName;
        this.price = price;
        this.totalQuantity = totalQuantity;
        this.stockQuantity = stockQuantity;
        this.salesYn = salesYn;
        this.regDate = regDate;
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