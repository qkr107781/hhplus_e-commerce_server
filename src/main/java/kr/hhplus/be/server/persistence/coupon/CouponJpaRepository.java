package kr.hhplus.be.server.persistence.coupon;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.coupon.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CouponJpaRepository extends JpaRepository<Coupon,Long> {

    Coupon findByCouponId(long couponId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Coupon findById(long couponId);

    List<Coupon> findByCouponStatus(String status);

    @Modifying
    @Query("UPDATE Coupon c SET c.remainingCouponAmount = c.remainingCouponAmount - 1 " +
            "WHERE c.couponId = :couponId AND c.remainingCouponAmount > 0")
    int decreaseRemainingCoupon(@Param("couponId") Long couponId);
}
