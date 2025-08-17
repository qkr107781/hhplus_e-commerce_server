package kr.hhplus.be.server.application.coupon.repository;

import kr.hhplus.be.server.domain.coupon.CouponIssuedInfo;

import java.util.List;

public interface CouponIssuedInfoRepository {

    CouponIssuedInfo findByCouponIdAndUserId(long couponId, long userId);

    CouponIssuedInfo useCoupon(CouponIssuedInfo couponIssuedInfo);

    CouponIssuedInfo unuseCoupon(CouponIssuedInfo couponIssuedInfo);

    CouponIssuedInfo issuingCoupon(CouponIssuedInfo couponIssuedInfo);

    List<CouponIssuedInfo> findByUserId(long userId);

}
