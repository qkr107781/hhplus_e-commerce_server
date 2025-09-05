package kr.hhplus.be.server.application.coupon.repository;

import kr.hhplus.be.server.domain.coupon.Coupon;

import java.util.List;

public interface CouponRepository{

    Coupon findByCouponId(long couponId);

    Coupon findByCouponIdWithLock(long couponId);

    List<Coupon> findByCouponStatus(String status);

    int decreaseRemainingCoupon(long couponId);
}
