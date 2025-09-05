package kr.hhplus.be.server.persistence.coupon;

import kr.hhplus.be.server.domain.coupon.CouponOutbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponOutboxJpaRepository extends JpaRepository<CouponOutbox,Long> {
}
