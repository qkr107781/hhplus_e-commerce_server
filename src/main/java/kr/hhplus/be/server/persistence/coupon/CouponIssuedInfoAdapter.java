package kr.hhplus.be.server.persistence.coupon;

import kr.hhplus.be.server.application.coupon.repository.CouponIssuedInfoRepository;
import kr.hhplus.be.server.domain.coupon.CouponIssuedInfo;

public class CouponIssuedInfoAdapter implements CouponIssuedInfoRepository {
    private final CouponIssuedInfoJpaRepository couponIssuedInfoJpaRepository;

    public CouponIssuedInfoAdapter(CouponIssuedInfoJpaRepository couponIssuedInfoJpaRepository) {
        this.couponIssuedInfoJpaRepository = couponIssuedInfoJpaRepository;
    }

    @Override
    public CouponIssuedInfo findByCouponIdAndUserId(long couponId, long userId) {
        return couponIssuedInfoJpaRepository.findByCouponIdAndUserId(couponId,userId);
    }

    @Override
    public CouponIssuedInfo useCoupon(CouponIssuedInfo couponIssuedInfo) {
        return couponIssuedInfoJpaRepository.save(couponIssuedInfo);
    }

    @Override
    public CouponIssuedInfo issuingCoupon(CouponIssuedInfo couponIssuedInfo) {
        return couponIssuedInfoJpaRepository.save(couponIssuedInfo);
    }

    @Override
    public CouponIssuedInfo findByUserId(long userId) {
        return couponIssuedInfoJpaRepository.findByUserId(userId);
    }

}
