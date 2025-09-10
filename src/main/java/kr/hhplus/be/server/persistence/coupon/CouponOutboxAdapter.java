package kr.hhplus.be.server.persistence.coupon;

import kr.hhplus.be.server.application.coupon.repository.CouponOutboxRepository;
import kr.hhplus.be.server.domain.coupon.CouponOutbox;
import org.springframework.stereotype.Component;

@Component
public class CouponOutboxAdapter implements CouponOutboxRepository {

    private final CouponOutboxJpaRepository couponOutboxJpaRepository;

    public CouponOutboxAdapter(CouponOutboxJpaRepository couponOutboxJpaRepository) {
        this.couponOutboxJpaRepository = couponOutboxJpaRepository;
    }

    @Override
    public CouponOutbox save(CouponOutbox couponOutbox) {
        return couponOutboxJpaRepository.save(couponOutbox);
    }

    @Override
    public int updateStatus(long couponId, long userId, String status) {
        return couponOutboxJpaRepository.updateStatus(couponId,userId,status);
    }
}
