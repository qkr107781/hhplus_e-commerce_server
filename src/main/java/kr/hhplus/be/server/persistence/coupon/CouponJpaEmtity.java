package kr.hhplus.be.server.persistence.coupon;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name="coupon")
@Getter
@NoArgsConstructor
public class CouponJpaEmtity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_id")
    private Long couponId;

    @Column(name = "coupon_name", length = 50, nullable = false)
    private String couponName;

    @Column(name = "discount_price", nullable = false)
    private Long discountPrice;

    @Column(name = "total_coupon_amount", nullable = false)
    private Long totalCouponAmount;

    @Column(name = "remaining_coupon_amount", nullable = false)
    private Long remainingCouponAmount;

    @Column(name = "min_use_price", nullable = false)
    private Long minUsePrice;

    @Column(name = "issuance_start_time", nullable = false)
    private LocalDateTime issuanceStartTime;

    @Column(name = "issuance_end_time", nullable = false)
    private LocalDateTime issuanceEndTime;

    @Column(name = "use_limit_time", nullable = false)
    private Long useLimitTime;

    @Column(name = "coupon_status", length = 10, nullable = false)
    private String couponStatus;

    @Column(name = "reg_date", nullable = false)
    private LocalDateTime regDate;

    @Builder
    public CouponJpaEmtity(Long couponId, String couponName, Long discountPrice, Long totalCouponAmount,
                           Long remainingCouponAmount, Long minUsePrice, LocalDateTime issuanceStartTime,
                           LocalDateTime issuanceEndTime, Long useLimitTime, String couponStatus, LocalDateTime regDate) {
        this.couponId = couponId;
        this.couponName = couponName;
        this.discountPrice = discountPrice;
        this.totalCouponAmount = totalCouponAmount;
        this.remainingCouponAmount = remainingCouponAmount;
        this.minUsePrice = minUsePrice;
        this.issuanceStartTime = issuanceStartTime;
        this.issuanceEndTime = issuanceEndTime;
        this.useLimitTime = useLimitTime;
        this.couponStatus = couponStatus;
        this.regDate = regDate;
    }
}