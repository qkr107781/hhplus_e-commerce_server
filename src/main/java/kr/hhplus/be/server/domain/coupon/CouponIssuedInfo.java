package kr.hhplus.be.server.domain.coupon;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "coupon_issued_info")
@Getter
@NoArgsConstructor
public class CouponIssuedInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_issued_id")
    private Long coupon_issued_id;

    @Column(name = "coupon_id", nullable = false)
    private Long coupon_id;

    @Column(name = "user_id", nullable = false)
    private Long user_id;

    @Column(name = "use_yn", length = 1, nullable = false)
    private String use_yn;

    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issued_at;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime end_date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="coupon_id", nullable = false)
    private Coupon coupon;

    @Builder
    public CouponIssuedInfo(Long coupon_issued_id, Long coupon_id, Long user_id, String use_yn, LocalDateTime issued_at, LocalDateTime end_date, Coupon coupon) {
        this.coupon_issued_id = coupon_issued_id;
        this.coupon_id = coupon_id;
        this.user_id = user_id;
        this.use_yn = use_yn;
        this.issued_at = issued_at;
        this.end_date = end_date;
        this.coupon = coupon;
    }

    public boolean validateCoupon(CouponIssuedInfo couponIssuedInfo,long user_id){
        if(couponIssuedInfo.coupon.getRemaining_coupon_amount() == 0){
            return false;
        }
        if(couponIssuedInfo.coupon.getIssuance_start_time().isAfter(LocalDateTime.now()) &&
            couponIssuedInfo.coupon.getIssuance_end_time().isBefore(LocalDateTime.now())){
            return false;
        }
        if(user_id == couponIssuedInfo.user_id){
            return false;
        }
        return true;
    }
}