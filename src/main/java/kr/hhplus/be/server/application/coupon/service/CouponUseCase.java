package kr.hhplus.be.server.application.coupon.service;

import kr.hhplus.be.server.application.coupon.dto.CouponResponse;
import org.redisson.api.RStream;
import org.redisson.api.StreamMessageId;

import java.util.List;
import java.util.Map;

public interface CouponUseCase {

    /**
     * 쿠폰 발급
     * @param couponId: 쿠폰 ID
     * @param userId: 사용자 ID
     * @return CouponIssuedInfo
     */
    CouponResponse.Issue issuingCoupon(long couponId, long userId) throws Exception;

    /**
     * 본인 쿠폰 조회
     * @param userId: 사용자 ID
     * @return CouponResponse.SelectByUserId
     */
    List<CouponResponse.SelectByUserId> selectCouponByUserId(long userId);

    /**
     * 쿠폰 상태별 조회
     * @param couponStatus: 쿠폰 발급 상태
     * @return CouponResponse.SelectByStatus
     */
    List<CouponResponse.SelectByStatus> selectCouponByStatus(String couponStatus);

    /**
     * Redis 자료구조 기반 발급 요청 (Streams)
     * @param couponId: 쿠폰 ID
     * @param userId: 사용자 ID
     * @return 발급 요청 결과
     */
    String issuingCouponAsync(long couponId, long userId);

    /**
     *  Redis 자료구조 기반 발급 처리(Streams)
     * @param queueStream: Streams Queue
     * @param groupName: Consumer Group Name
     * @param messages: 추출 메세지
     */
    void couponIssueProcess(RStream<String, String> queueStream, String groupName, Map<StreamMessageId, Map<String, String>> messages);
}
