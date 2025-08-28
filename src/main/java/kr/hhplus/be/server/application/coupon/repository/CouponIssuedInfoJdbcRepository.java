package kr.hhplus.be.server.application.coupon.repository;

import kr.hhplus.be.server.application.coupon.dto.CouponRequest;

import java.util.List;

public interface CouponIssuedInfoJdbcRepository {

    void bulkInsertCouponIssuedInfo(List<CouponRequest.Issue> coupons);

}
