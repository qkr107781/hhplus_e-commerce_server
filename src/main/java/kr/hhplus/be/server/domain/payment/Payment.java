package kr.hhplus.be.server.domain.payment;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "payment_price", nullable = false)
    private Long paymentPrice;

    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate;

    @Builder
    public Payment(Long paymentId, Long userId, Long orderId, Long paymentPrice, LocalDateTime paymentDate) {
        this.paymentId = paymentId;
        this.userId = userId;
        this.orderId = orderId;
        this.paymentPrice = paymentPrice;
        this.paymentDate = paymentDate;
    }
}