package kr.hhplus.be.server.application.coupon.repository;

import kr.hhplus.be.server.domain.coupon.CouponOutbox;

public interface CouponOutboxRepository {

    CouponOutbox save(CouponOutbox couponOutbox);

    int updateStatus(long couponId, long userId, String status);

}
