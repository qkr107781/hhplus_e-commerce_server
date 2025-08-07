package kr.hhplus.be.server.domain.product;

import jakarta.persistence.*;
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

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "option_name", length = 50, nullable = false)
    private String optionName;

    @Column(name = "price", nullable = false)
    private Long price;

    @Column(name = "sales_yn", length = 1, nullable = false)
    private String salesYn;

    @Column(name = "total_quantity", nullable = false)
    private Long totalQuantity;

    @Column(name = "stock_quantity", nullable = false)
    private Long stockQuantity;

    @Column(name = "reg_date", nullable = false)
    private LocalDateTime regDate;

    @Builder
    public ProductOption(Long productOptionId, Long productId, String optionName, Long price, String salesYn, Long totalQuantity, Long stockQuantity, LocalDateTime regDate) {
        this.productOptionId = productOptionId;
        this.productId = productId;
        this.optionName = optionName;
        this.price = price;
        this.salesYn = salesYn;
        this.totalQuantity = totalQuantity;
        this.stockQuantity = stockQuantity;
        this.regDate = regDate;
    }

    /**
     * 재고 차감
     * @throws Exception
     */
    public void decreaseProductQuantity(long decreaseQuantity) throws Exception {
        if(stockQuantity == 0 || stockQuantity - decreaseQuantity < 0){
            throw new Exception("stock empty");
        } else{
            this.stockQuantity = stockQuantity - decreaseQuantity;
        }
    }

    /**
     * 재고 복구
     * @param restoreQuantity: 복구 갯수
     * @throws Exception
     */
    public void restoreProductQuantity(long restoreQuantity) throws Exception {
        if(restoreQuantity == 0){
            throw new Exception("restore stock empty");
        }
        if(this.totalQuantity < this.stockQuantity + restoreQuantity){
            throw new Exception("restore stock over");
        }
        this.stockQuantity = stockQuantity + restoreQuantity;
    }
}