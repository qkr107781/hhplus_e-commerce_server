package kr.hhplus.be.server.domain.coupon;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "coupon_outbox"
        ,uniqueConstraints = {
        @UniqueConstraint(
                name="unique_coupon_outbox_coupon_id_user_id",
                columnNames={"coupon_id","user_id"}
        )}
)
@Getter
@NoArgsConstructor
public class CouponOutbox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "outbox_id")
    private Long outboxId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "coupon_id", nullable = false)
    private Long couponId;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "topic_key", nullable = false)
    private String topicKey;

    @Column(name = "payload", nullable = false)
    private String payload;


    @Builder
    public CouponOutbox(Long outboxId, Long userId, Long couponId, String status, LocalDateTime createdAt, String topicKey, String payload) {
        this.outboxId = outboxId;
        this.userId = userId;
        this.couponId = couponId;
        this.status = status;
        this.createdAt = createdAt;
        this.topicKey = topicKey;
        this.payload = payload;
    }
}
