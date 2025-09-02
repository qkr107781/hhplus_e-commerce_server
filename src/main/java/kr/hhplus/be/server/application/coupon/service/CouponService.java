package kr.hhplus.be.server.application.coupon.service;

import kr.hhplus.be.server.application.coupon.dto.CouponRequest;
import kr.hhplus.be.server.application.coupon.dto.CouponResponse;
import kr.hhplus.be.server.application.coupon.repository.CouponIssuedInfoJdbcRepository;
import kr.hhplus.be.server.application.coupon.repository.CouponIssuedInfoRepository;
import kr.hhplus.be.server.application.coupon.repository.CouponRepository;
import kr.hhplus.be.server.application.redis.repository.RedisRepository;
import kr.hhplus.be.server.common.redis.DistributedFairLock;
import kr.hhplus.be.server.common.redis.RedisKeys;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponIssuedInfo;
import org.redisson.api.*;
import org.redisson.client.codec.StringCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CouponService implements CouponUseCase{

    private static final Logger log = LoggerFactory.getLogger(CouponService.class);

    private final CouponIssuedInfoRepository couponIssuedInfoRepository;
    private final CouponRepository couponRepository;
    private final CouponIssuedInfoJdbcRepository couponIssuedInfoJdbcRepository;
    private final RedisRepository redisRepository;

    public CouponService(CouponIssuedInfoRepository couponIssuedInfoRepository, CouponRepository couponRepository, CouponIssuedInfoJdbcRepository couponIssuedInfoJdbcRepository, RedisRepository redisRepository) {
        this.couponIssuedInfoRepository = couponIssuedInfoRepository;
        this.couponRepository = couponRepository;
        this.couponIssuedInfoJdbcRepository = couponIssuedInfoJdbcRepository;
        this.redisRepository = redisRepository;
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

    /**
     * Redis 자료구조 기반 발급 요청 (Streams)
     */
    @Override
    public String issuingCouponAsync(long couponId, long userId) {

        String hashesKey = RedisKeys.COUPON_META.format(couponId);
        String setsKey = RedisKeys.COUPON_QUEUE.format(couponId);
        // 유효성 검사 로직 (시간 관련)은 Lua 스크립트 외부에서 처리
        RMap<String, String> metaHashes = redisRepository.getHashes(hashesKey,StringCodec.INSTANCE);
        if (metaHashes == null) {
            return "발급 불가";
        }
        LocalDateTime startDate = LocalDateTime.parse(metaHashes.get("start_date"));
        LocalDateTime endDate = LocalDateTime.parse(metaHashes.get("end_date"));
        LocalDateTime nowDate = LocalDateTime.now();
        if (startDate.isAfter(nowDate) || endDate.isBefore(nowDate)) {
            return "발급 기간이 아닙니다.";
        }

        //발급 요청 Sets TTL Seconds -> 발급 종료일 - 발급 시자일 + 1시간
        long setsTTLSeconds = ChronoUnit.SECONDS.between(startDate, endDate) + 3600;

        Long resultCode = redisRepository.requestCouponIssue(hashesKey,setsKey, RedisKeys.COUPON_ISSUE_JOB.format(),String.valueOf(couponId),String.valueOf(userId),String.valueOf(setsTTLSeconds));
        if (resultCode.intValue() == 3) return "쿠폰이 모두 소진 됐습니다.";
        if (resultCode.intValue() == 2) return "중복된 발급 요청 입니다.";
        if (resultCode.intValue() == 0) return "오류 발생";

        return "발급 요청이 접수되었습니다. 발급 결과는 추후 확인해주세요.";
    }

    /**
     * Redis 자료구조 기반 발급 처리(Streams)
     */
    @Override
    public void couponIssueProcess(RStream<String, String> queueStream, String groupName, Map<StreamMessageId, Map<String, String>> messages){
        // DB insert를 위한 리스트(coupon_issued_info 테이블)
        List<CouponRequest.Issue> bulkInsertList = new ArrayList<>();
        // 삭제할 메시지 ID를 저장할 리스트
        List<StreamMessageId> messageIdsToDelete = new ArrayList<>();

        for (Map.Entry<StreamMessageId, Map<String, String>> entry : messages.entrySet()) {
            StreamMessageId messageId = entry.getKey();
            Map<String, String> data = entry.getValue();

            String couponId = data.get("couponId");
            String userId = data.get("userId");

            // 재고 감소 처리
            Number result = redisRepository.decStockFromRedis(couponId);
            if (result.intValue() != 1) {
                continue;
            }

            // 메시지 ack
            queueStream.ack(groupName, messageId);

            // DB bulk insert 준비
            bulkInsertList.add(new CouponRequest.Issue(Long.parseLong(userId),Long.parseLong(couponId)));

            // 삭제 리스트에 ID 추가
            messageIdsToDelete.add(messageId);
        }

        // 배치 DB insert
        if (!bulkInsertList.isEmpty()) {
            try {
                couponIssuedInfoJdbcRepository.bulkInsertCouponIssuedInfo(bulkInsertList);

                // DB 저장 성공 시에만 스트림에서 메시지 삭제
                queueStream.remove(messageIdsToDelete.toArray(new StreamMessageId[0]));
            } catch (Exception e) {
                log.error("Bulk insert failed, rolling back stock", e);
                // DB 실패 시 Redis 잔여 쿠폰 복구
                for (CouponRequest.Issue item : bulkInsertList) {
                    String couponId =  String.valueOf(item.couponId());
                    redisRepository.incStockFromRedis(couponId);
                }
            }
        }
    }
}
