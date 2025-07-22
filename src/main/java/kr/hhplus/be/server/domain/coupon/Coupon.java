package kr.hhplus.be.server.domain.coupon;

import java.time.LocalDateTime;

public class Coupon {

    private Long couponId;
    private String couponName;
    private Long discountPrice;
    private Long totalCouponAmount;
    private Long remainingCouponAmount;
    private Long minUsePrice;
    private LocalDateTime issuanceStartTime;
    private LocalDateTime issuanceEndTime;
    private Long useLimitTime;
    private String couponStatus;
    private LocalDateTime regDate;

    public Coupon(Long couponId, String couponName, Long discountPrice, Long totalCouponAmount, Long remainingCouponAmount, Long minUsePrice, LocalDateTime issuanceStartTime, LocalDateTime issuanceEndTime, Long useLimitTime, String couponStatus, LocalDateTime regDate) {
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

    public Long getCouponId() {
        return couponId;
    }

    public String getCouponName() {
        return couponName;
    }

    public Long getDiscountPrice() {
        return discountPrice;
    }

    public Long getTotalCouponAmount() {
        return totalCouponAmount;
    }

    public Long getRemainingCouponAmount() {
        return remainingCouponAmount;
    }

    public Long getMinUsePrice() {
        return minUsePrice;
    }

    public LocalDateTime getIssuanceStartTime() {
        return issuanceStartTime;
    }

    public LocalDateTime getIssuanceEndTime() {
        return issuanceEndTime;
    }

    public Long getUseLimitTime() {
        return useLimitTime;
    }

    public String getCouponStatus() {
        return couponStatus;
    }

    public LocalDateTime getRegDate() {
        return regDate;
    }
}