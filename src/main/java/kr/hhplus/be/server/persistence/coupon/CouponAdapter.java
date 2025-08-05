package kr.hhplus.be.server.persistence.coupon;

import kr.hhplus.be.server.application.coupon.repository.CouponRepository;
import kr.hhplus.be.server.domain.coupon.Coupon;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CouponAdapter implements CouponRepository {
    private final CouponJpaRepository couponJpaRepository;

    public CouponAdapter(CouponJpaRepository couponJpaRepository) {
        this.couponJpaRepository = couponJpaRepository;
    }

    @Override
    public Coupon findByCouponId(long couponId) {
        return couponJpaRepository.findByCouponId(couponId);
    }

    @Override
    public List<Coupon> findByCouponStatus(String status) {
        return couponJpaRepository.findByCouponStatus(status);
    }
}
