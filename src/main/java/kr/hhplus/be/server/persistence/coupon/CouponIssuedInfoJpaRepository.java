package kr.hhplus.be.server.persistence.coupon;

import kr.hhplus.be.server.domain.coupon.CouponIssuedInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CouponIssuedInfoJpaRepository extends JpaRepository<CouponIssuedInfo,Long> {

    CouponIssuedInfo findByCouponIdAndUserId(long couponId,long userId);

    List<CouponIssuedInfo> findByUserId(long userId);

}
