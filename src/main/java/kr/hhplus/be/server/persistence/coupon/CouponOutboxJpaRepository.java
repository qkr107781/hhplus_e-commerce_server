package kr.hhplus.be.server.persistence.coupon;

import kr.hhplus.be.server.domain.coupon.CouponOutbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponOutboxJpaRepository extends JpaRepository<CouponOutbox,Long> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update CouponOutbox o set o.status = :status where o.userId = :userId and o.couponId = :couponId")
    int updateStatus(@Param("couponId") long couponId, @Param("userId") long userId, @Param("status") String status);
}
