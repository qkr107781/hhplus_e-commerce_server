package kr.hhplus.be.server.domain.order;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "`order`",
        indexes = {
                @Index(name = "idx_order_order_status_order_date", columnList = "order_status, order_date"),
                @Index(name = "idx_order_order_id_version", columnList = "order_id, version")
        }
)
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

    @Version
    @Column(columnDefinition = "BIGINT DEFAULT 0")//테스트컨테이너에서 DDL 자동생성 시 기본값 부여를 위해
    private Long version;

    @Builder
    public Order(Long orderId, Long userId, Long couponId, Long couponDiscountPrice, Long totalPrice, String orderStatus, LocalDateTime orderDate, Long version) {
        this.orderId = orderId;
        this.userId = userId;
        this.couponId = couponId;
        this.couponDiscountPrice = couponDiscountPrice;
        this.totalPrice = totalPrice;
        this.orderStatus = orderStatus;
        this.orderDate = orderDate;
        this.version = version;
    }

    public void updateOrderStatusToPayment(){
        this.orderStatus = "payment_completed";
    }

    public void cancelOrder(){
        orderStatus = "cancel_order";
    }
}