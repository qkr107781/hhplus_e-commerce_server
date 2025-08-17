package kr.hhplus.be.server.domain.order;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "order_product",
        indexes = {
                @Index(name = "idx_order_product_order_id", columnList = "order_id"),
                @Index(name = "idx_order_product_product_option_id_product_quantity", columnList = "product_option_id, product_quantity")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_product_id")
    private Long orderProductId;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "product_option_id", nullable = false)
    private Long productOptionId;

    @Column(name = "product_quantity", nullable = false)
    private Long productQuantity;

    @Column(name = "product_price", nullable = false)
    private Long productPrice;

    @Builder
    public OrderProduct(Long orderProductId, Long orderId, Long productId, Long productOptionId, Long productQuantity, Long productPrice) {
        this.orderProductId = orderProductId;
        this.orderId = orderId;
        this.productId = productId;
        this.productOptionId = productOptionId;
        this.productQuantity = productQuantity;
        this.productPrice = productPrice;
    }
}