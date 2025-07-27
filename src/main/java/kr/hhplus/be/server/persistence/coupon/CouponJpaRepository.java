package kr.hhplus.be.server.persistence.coupon;

import kr.hhplus.be.server.domain.coupon.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponJpaRepository extends JpaRepository<Coupon,Long> {

    Coupon findByCouponId(long couponId);

    Coupon findByCouponStatus(String status);
}
