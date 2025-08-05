package kr.hhplus.be.server.persistence.coupon;

import kr.hhplus.be.server.domain.coupon.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CouponJpaRepository extends JpaRepository<Coupon,Long> {

    Coupon findByCouponId(long couponId);

    List<Coupon> findByCouponStatus(String status);
}
