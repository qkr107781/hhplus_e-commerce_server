package kr.hhplus.be.server.persistence.coupon;

import kr.hhplus.be.server.application.coupon.dto.CouponRequest;
import kr.hhplus.be.server.application.coupon.repository.CouponIssuedInfoJdbcRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class CouponIssuedInfoJdbcAdapter implements CouponIssuedInfoJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public CouponIssuedInfoJdbcAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * CouponIssuedInfo 엔티티를 고성능 Batch Insert
     * @param coupons insert할 쿠폰 발급 정보 리스트
     */
    @Override
    public void bulkInsertCouponIssuedInfo(List<CouponRequest.Issue> coupons) {
        String couponIssueSql = "INSERT INTO coupon_issued_info " +
                "(user_id, use_yn, issued_at, end_date, coupon_id) " +
                "VALUES (?, ?, ?, ?, ?)";

        int batchSize = 100; // 한 번에 100건씩 insert

        jdbcTemplate.batchUpdate(couponIssueSql, coupons, batchSize,
                (ps, coupon) -> {
                    LocalDateTime now = LocalDateTime.now();
                    long couponId = coupon.couponId();
                    long userId = coupon.userId();

                    ps.setLong(1, userId);
                    ps.setString(2, "N");
                    ps.setTimestamp(3, Timestamp.valueOf(now));
                    ps.setTimestamp(4, Timestamp.valueOf(now.plusDays(1)));
                    ps.setLong(5, couponId);
                }
        );
        for(CouponRequest.Issue coupon : coupons){
            long couponId = coupon.couponId();

            String couponDecSql = "update coupon set remaining_coupon_amount = remaining_coupon_amount - 1 where coupon_id = ?";
            jdbcTemplate.update(couponDecSql, couponId);
        }
    }
}
