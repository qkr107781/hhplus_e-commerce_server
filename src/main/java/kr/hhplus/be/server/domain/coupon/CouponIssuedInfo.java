package kr.hhplus.be.server.domain.coupon;

import java.time.LocalDateTime;

public class CouponIssuedInfo {

    private Long couponIssuedId;
    private Long userId;
    private String useYn;
    private LocalDateTime issuedAt;
    private LocalDateTime endDate;
    private Coupon coupon;

    public CouponIssuedInfo(Long couponIssuedId, Long userId, String useYn, LocalDateTime issuedAt, LocalDateTime endDate, Coupon coupon) {
        this.couponIssuedId = couponIssuedId;
        this.userId = userId;
        this.useYn = useYn;
        this.issuedAt = issuedAt;
        this.endDate = endDate;
        this.coupon = coupon;
    }

    public Long getCouponIssuedId() {
        return couponIssuedId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUseYn() {
        return useYn;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public Coupon getCoupon() {
        return coupon;
    }

}