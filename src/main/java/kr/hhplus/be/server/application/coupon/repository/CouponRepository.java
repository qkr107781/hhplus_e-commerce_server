package kr.hhplus.be.server.application.coupon.repository;

import kr.hhplus.be.server.domain.coupon.Coupon;

public interface CouponRepository{

    Coupon findByCouponId(long couponId);

    Coupon findByStatus(String status);
}
