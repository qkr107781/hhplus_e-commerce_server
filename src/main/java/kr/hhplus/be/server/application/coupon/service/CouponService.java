package kr.hhplus.be.server.application.coupon.service;

import kr.hhplus.be.server.application.coupon.dto.CouponResponse;
import kr.hhplus.be.server.application.coupon.repository.CouponIssuedInfoRepository;
import kr.hhplus.be.server.application.coupon.repository.CouponRepository;
import kr.hhplus.be.server.common.redis.DistributedFairLock;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponIssuedInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CouponService implements CouponUseCase{

    private static final Logger log = LoggerFactory.getLogger(CouponService.class);

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
        Coupon coupon = couponRepository.findByCouponId(requestCouponId);

        if(couponIssuedInfo.validateCouponUsage(totalOrderPrice,coupon.getDiscountPrice())){
            //쿠폰 사용
            couponIssuedInfo.useCoupon();


            couponIssuedInfo = couponIssuedInfoRepository.useCoupon(couponIssuedInfo);
        }

        return couponIssuedInfo;
    }

    /**
     * 쿠폰 미사용 처리
     * @param requestCouponId:쿠폰 ID
     * @param requestUserId:유저 ID
     * @return couponIssuedInfo
     */
    public CouponIssuedInfo restoreCoupon(long requestUserId, long requestCouponId){
        CouponIssuedInfo couponIssuedInfo = couponIssuedInfoRepository.findByCouponIdAndUserId(requestCouponId,requestUserId);
        couponIssuedInfo.unuseCoupon();
        return couponIssuedInfoRepository.unuseCoupon(couponIssuedInfo);
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
     * @throws Exception: 유효성 검사 예외
     */
    @DistributedFairLock(key = "'coupon:issue:' + #couponId")
    @Transactional
    @Override
    public CouponResponse.Issue issuingCoupon(long couponId,long userId) throws Exception {
        log.info("쿠폰 발급 트랜잭션 시작: userId: {}", userId);
        //발급 준비
//        Coupon coupon = couponRepository.findByCouponIdWithLock(couponId);
        Coupon coupon = couponRepository.findByCouponId(couponId);
        CouponIssuedInfo issuingCoupon = CouponIssuedInfo.builder()
                .userId(userId)
                .useYn("N")
                .issuedAt(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusHours(24L))
                .couponId(coupon.getCouponId())
                .build();

        //쿠폰 발급 유효성 검증
        coupon.validateCouponIssuance();

        //쿠폰 잔여 갯수 차감
        coupon.decreaseCoupon();

        log.info("쿠폰 발급 트랜잭션 종료");
        return CouponResponse.Issue.from(couponIssuedInfoRepository.issuingCoupon(issuingCoupon),coupon);
    }

    @Override
    public List<CouponResponse.SelectByUserId> selectCouponByUserId(long userId) {
        List<CouponIssuedInfo> couponIssuedInfoList = couponIssuedInfoRepository.findByUserId(userId);
        List<Coupon> couponList = new ArrayList<>();
        for (CouponIssuedInfo couponIssuedInfo : couponIssuedInfoList){
            couponList.add(couponRepository.findByCouponId(couponIssuedInfo.getCouponId()));
        }
        return CouponResponse.SelectByUserId.from(couponIssuedInfoList,couponList);
    }

    @Override
    public List<CouponResponse.SelectByStatus> selectCouponByStatus(String couponStatus) {
        return CouponResponse.SelectByStatus.from(couponRepository.findByCouponStatus(couponStatus));
    }
}
