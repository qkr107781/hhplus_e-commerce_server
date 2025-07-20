package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.domain.coupon.CouponIssuedInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponIssuedInfoRepository extends JpaRepository<CouponIssuedInfo,Long> {

    int countByCouponIdAndUserId(long couponId,long userId);

}
