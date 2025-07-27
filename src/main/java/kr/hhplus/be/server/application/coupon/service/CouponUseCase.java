package kr.hhplus.be.server.application.coupon.service;

import kr.hhplus.be.server.application.coupon.dto.CouponResponse;

public interface CouponUseCase {

    /**
     * 쿠폰 발급
     * @param couponId: 쿠폰 ID, 사용자 ID
     * @return CouponIssuedInfo
     */
    CouponResponse.Issue issuingCoupon(long couponId, long userId) throws Exception;

    /**
     * 본인 쿠폰 조회
     * @param userId: 사용자 ID
     * @return CouponResponse.SelectByUserId
     */
    CouponResponse.SelectByUserId selectCouponByUserId(long userId);

    /**
     * 쿠폰 상태별 조회
     * @param couponStatus: 쿠폰 발급 상태
     * @return CouponResponse.SelectByStatus
     */
    CouponResponse.SelectByStatus selectCouponByStatus(String couponStatus);
}
