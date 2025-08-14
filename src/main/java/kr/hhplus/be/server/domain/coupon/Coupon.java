package kr.hhplus.be.server.domain.coupon;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "coupon",
        indexes = {
                @Index(name = "idx_coupon_coupon_status", columnList = "coupon_status")
        }
)
@Getter
@NoArgsConstructor
public class Coupon {

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

    @Column(name = "issuance_start_time", nullable = false)
    private LocalDateTime issuanceStartTime;

    @Column(name = "issuance_end_time", nullable = false)
    private LocalDateTime issuanceEndTime;

    @Column(name = "coupon_status", length = 10, nullable = false)
    private String couponStatus;

    @Column(name = "reg_date", nullable = false)
    private LocalDateTime regDate;

    @Builder
    public Coupon(Long couponId, String couponName, Long discountPrice, Long totalCouponAmount,
                  Long remainingCouponAmount, LocalDateTime issuanceStartTime,
                  LocalDateTime issuanceEndTime, String couponStatus, LocalDateTime regDate) {
        this.couponId = couponId;
        this.couponName = couponName;
        this.discountPrice = discountPrice;
        this.totalCouponAmount = totalCouponAmount;
        this.remainingCouponAmount = remainingCouponAmount;
        this.issuanceStartTime = issuanceStartTime;
        this.issuanceEndTime = issuanceEndTime;
        this.couponStatus = couponStatus;
        this.regDate = regDate;
    }

    /**
     * 쿠폰 발급 유효성 검증
     * @throws Exception
     */
    public void validateCouponIssuance() throws Exception {
        if(this.remainingCouponAmount == 0){
            throw new Exception("empty remaining coupon");
        }
        LocalDateTime now = LocalDateTime.now();
        if(this.issuanceStartTime.isAfter(now) || this.issuanceEndTime.isBefore(now)){
            throw new Exception("not issuing time");
        }
        if(!this.couponStatus.equals("issuing")){
            throw new Exception("not issuing status");
        }
    }

    /**
     * 쿠폰 갯수 차감
     */
    public void decreaseCoupon(){
        this.remainingCouponAmount = this.remainingCouponAmount - 1L;
    }
}