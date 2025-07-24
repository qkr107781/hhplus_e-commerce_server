package kr.hhplus.be.server.persistence.coupon;

import kr.hhplus.be.server.domain.coupon.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponJpaRepository extends JpaRepository<Coupon,Long> {

    Coupon findByCouponId(long couponId);

    Coupon findByStatus(String status);
}
