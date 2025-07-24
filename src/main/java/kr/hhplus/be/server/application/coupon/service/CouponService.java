package kr.hhplus.be.server.application.coupon.service;

import kr.hhplus.be.server.application.coupon.dto.CouponResponse;
import kr.hhplus.be.server.application.coupon.repository.CouponIssuedInfoRepository;
import kr.hhplus.be.server.application.coupon.repository.CouponRepository;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponIssuedInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CouponService implements CouponUseCase{

    private final CouponIssuedInfoRepository couponIssuedInfoRepository;
    private final CouponRepository couponRepository;

    public CouponService(CouponIssuedInfoRepository couponIssuedInfoRepository, CouponRepository couponRepository) {
        this.couponIssuedInfoRepository = couponIssuedInfoRepository;
        this.couponRepository = couponRepository;
    }

    /**
     * 쿠폰 사용
     * @param requestCouponId:쿠폰 ID
     * @param requestUserId:유저 ID
     * @param totalOrderPrice:총 주문 금액
     * @return couponIssuedInfo
     */
    public CouponIssuedInfo useCoupon(long requestCouponId, long requestUserId, long totalOrderPrice) {
        //쿠폰 사용 유효성 검증
        CouponIssuedInfo couponIssuedInfo = couponIssuedInfoRepository.findByCouponIdAndUserId(requestCouponId,requestUserId);
        if(couponIssuedInfo.validateCouponUsage(totalOrderPrice)){
            //쿠폰 사용
            couponIssuedInfo.useCoupon();
            couponIssuedInfo = couponIssuedInfoRepository.useCoupon(couponIssuedInfo);
        }

        return couponIssuedInfo;
    }

    /**
     * 쿠폰 ID로 조회
     * @param couponId:쿠폰 ID
     * @return Coupon
     */
    public Coupon selectCouponByCouponId(long couponId){
        return couponRepository.findByCouponId(couponId);
    }

    /**
     * 쿠폰 ID, 사용자 ID로 소유 쿠폰 조회
     * @param couponId: 쿠폰 ID
     * @param userId: 사용자 ID
     * @return CouponIssuedInfo
     */
    public CouponIssuedInfo selectCouponByCouponIdAndUserId(long couponId, long userId){
        return couponIssuedInfoRepository.findByCouponIdAndUserId(couponId,userId);
    }

    /**
     * 쿠폰 발급
     * @param couponId: 쿠폰 ID
     * @param userId: 사용자 ID
     * @return CouponIssuedInfo
     * @throws Exception
     */
    @Transactional
    @Override
    public CouponResponse.Issue issuingCoupon(long couponId,long userId) throws Exception {
        //발급 준비
        Coupon coupon = couponRepository.findByCouponId(couponId);
        CouponIssuedInfo issuingCoupon = CouponIssuedInfo.builder()
                .userId(userId)
                .useYn("N")
                .issuedAt(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusHours(coupon.getUseLimitTime()))
                .coupon(coupon)
                .build();

        //쿠폰 발급 유효성 검증
        issuingCoupon.getCoupon().validateCouponIssuance();

        //쿠폰 잔여 갯수 차감
        issuingCoupon.getCoupon().decreaseCoupon();

        return CouponResponse.Issue.from(couponIssuedInfoRepository.issuingCoupon(issuingCoupon));
    }

    @Override
    public CouponResponse.SelectByUserId selectCouponByUserId(long userId) {
        return CouponResponse.SelectByUserId.from(couponIssuedInfoRepository.findByUserId(userId));
    }

    @Override
    public CouponResponse.SelectByStatus selectCouponByStatus(String couponStatus) {
        return CouponResponse.SelectByStatus.from(couponRepository.findByStatus(couponStatus));
    }
}
