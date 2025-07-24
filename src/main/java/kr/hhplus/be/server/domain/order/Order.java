package kr.hhplus.be.server.domain.order;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "order_table")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "coupon_id")
    private Long couponId;

    @Column(name = "coupon_discount_price", nullable = false)
    private Long couponDiscountPrice;

    @Column(name = "total_price", nullable = false)
    private Long totalPrice;

    @Column(name = "order_status", length = 20, nullable = false)
    private String orderStatus;

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name="order_product_id", nullable = false)
    private List<OrderProduct> orderProducts;

    @Builder
    public Order(Long orderId, Long userId, Long couponId, Long couponDiscountPrice, Long totalPrice, String orderStatus, LocalDateTime orderDate, List<OrderProduct> orderProducts) {
        this.orderId = orderId;
        this.userId = userId;
        this.couponId = couponId;
        this.couponDiscountPrice = couponDiscountPrice;
        this.totalPrice = totalPrice;
        this.orderStatus = orderStatus;
        this.orderDate = orderDate;
        this.orderProducts = orderProducts;
    }

    public void updateOrderStatusToPayment(){
        this.orderStatus = "payment_completed";
    }
}