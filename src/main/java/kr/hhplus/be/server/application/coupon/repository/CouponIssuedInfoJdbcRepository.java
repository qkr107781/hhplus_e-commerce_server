package kr.hhplus.be.server.application.coupon.repository;

import java.util.List;

public interface CouponIssuedInfoJdbcRepository {

    void bulkInsertCouponIssuedInfo(List<String> coupons);

}
