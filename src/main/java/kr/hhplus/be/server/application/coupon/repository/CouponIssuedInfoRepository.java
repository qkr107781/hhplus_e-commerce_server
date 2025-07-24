package kr.hhplus.be.server.application.coupon.repository;

import kr.hhplus.be.server.domain.coupon.CouponIssuedInfo;

public interface CouponIssuedInfoRepository {

    CouponIssuedInfo findByCoupon_couponIdAndUserId(long couponId, long userId);

    CouponIssuedInfo useCoupon(CouponIssuedInfo couponIssuedInfo);

    CouponIssuedInfo issuingCoupon(CouponIssuedInfo couponIssuedInfo);

    CouponIssuedInfo findByUserId(long userId);

}
