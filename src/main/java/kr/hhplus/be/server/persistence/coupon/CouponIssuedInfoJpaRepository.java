package kr.hhplus.be.server.persistence.coupon;

import kr.hhplus.be.server.domain.coupon.CouponIssuedInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponIssuedInfoJpaRepository extends JpaRepository<CouponIssuedInfo,Long> {

    CouponIssuedInfo findByCouponIdAndUserId(long couponId,long userId);

    CouponIssuedInfo findByUserId(long userId);

}
