package kr.hhplus.be.server.domain.coupon;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CouponTest {

    @Test
    @DisplayName("[쿠폰 발급][잔여 쿠폰 0개]쿠폰이 모두 소진되어 발급 실패")
    void noCouponToIssue(){
        //Given
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String issuanceStartTimeStr = "2025-07-15 09:00:00";
        String issuanceEndTimeStr = "2025-07-15 11:00:00";
        String regDateStr = "2025-07-15 11:00:00";

        long couponId = 1L;
        String couponName = "신규 가입 쿠폰";
        long discountPrice = 1_000L;
        long totalCouponAmount = 30L;
        long remainingCouponAmount = 0L;
        long minUsePrice = 10_000L;
        LocalDateTime issuanceStartTime = LocalDateTime.parse(issuanceStartTimeStr, formatter);
        LocalDateTime issuanceEndTime = LocalDateTime.parse(issuanceEndTimeStr, formatter);
        long useLimitTime = 24L;
        String couponStatus = "issuing";
        LocalDateTime regDate = LocalDateTime.parse(regDateStr, formatter);

        Coupon remainingCouponEmpty = new Coupon(couponId,couponName,discountPrice,totalCouponAmount,remainingCouponAmount,minUsePrice,issuanceStartTime,issuanceEndTime,useLimitTime,couponStatus,regDate);

        //When
        Exception thrown = assertThrows(Exception.class,
                            remainingCouponEmpty::validateCouponIssuance,"empty remaining coupon");
        //Then
        assertTrue(thrown.getMessage().contains("empty remaining coupon"));
    }

    @Test
    @DisplayName("[쿠폰 발급][쿠폰 발급 가능 시간 아님]쿠폰 발급 시간 이전/이후 발급 요청하여 발급 실패")
    void notIssuingTime(){
        //Given
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String issuanceStartTimeStr = "2025-07-15 09:00:00";
        String issuanceEndTimeStr = "2025-07-15 11:00:00";
        String regDateStr = "2025-07-15 11:00:00";

        long couponId = 1L;
        String couponName = "신규 가입 쿠폰";
        long discountPrice = 1_000L;
        long totalCouponAmount = 30L;
        long remainingCouponAmount = 20L;
        long minUsePrice = 10_000L;
        LocalDateTime issuanceStartTime = LocalDateTime.parse(issuanceStartTimeStr, formatter);
        LocalDateTime issuanceEndTime = LocalDateTime.parse(issuanceEndTimeStr, formatter);
        long useLimitTime = 24L;
        String couponStatus = "issuing";
        LocalDateTime regDate = LocalDateTime.parse(regDateStr, formatter);

        Coupon notIssuingTimeCoupon = new Coupon(couponId,couponName,discountPrice,totalCouponAmount,remainingCouponAmount,minUsePrice,issuanceStartTime,issuanceEndTime,useLimitTime,couponStatus,regDate);

        //When
        Exception thrown = assertThrows(Exception.class,
                            notIssuingTimeCoupon::validateCouponIssuance,"not issuing time");
        //Then
        assertTrue(thrown.getMessage().contains("not issuing time"));
    }

    @Test
    @DisplayName("[쿠폰 발급][쿠폰 발급 상태 아님]쿠폰 발급 중(issuing) 상태가 아닌경우 발급 실패")
    void notIssuingStatus(){
        //Given
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String regDateStr = "2025-07-15 11:00:00";

        long couponId = 1L;
        String couponName = "신규 가입 쿠폰";
        long discountPrice = 1_000L;
        long totalCouponAmount = 30L;
        long remainingCouponAmount = 20L;
        long minUsePrice = 10_000L;
        LocalDateTime issuanceStartTime = LocalDateTime.now().minusHours(1);
        LocalDateTime issuanceEndTime = LocalDateTime.now().plusHours(1);
        long useLimitTime = 24L;
        String couponStatus = "closed";
        LocalDateTime regDate = LocalDateTime.parse(regDateStr, formatter);

        Coupon  notIssuingStatusCoupon = new Coupon(couponId,couponName,discountPrice,totalCouponAmount,remainingCouponAmount,minUsePrice,issuanceStartTime,issuanceEndTime,useLimitTime,couponStatus,regDate);

        //When
        Exception thrown = assertThrows(Exception.class,
                            notIssuingStatusCoupon::validateCouponIssuance,"not issuing status");
        //Then
        assertTrue(thrown.getMessage().contains("not issuing status"));
    }

    @Test
    @DisplayName("[쿠폰 발급][잔여 갯수 차감]쿠폰 발급 가능 시간, 잔여 수량 충분 -> 발급 성공하여 잔여 갯수 차감")
    void canIssueCoupon() throws Exception {
        //Given
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String regDateStr = "2025-07-15 11:00:00";

        long couponId = 1L;
        String couponName = "신규 가입 쿠폰";
        long discountPrice = 1_000L;
        long totalCouponAmount = 30L;
        long remainingCouponAmount = 10L;
        long minUsePrice = 10_000L;
        LocalDateTime issuanceStartTime = LocalDateTime.now().minusHours(1);
        LocalDateTime issuanceEndTime = LocalDateTime.now().plusHours(1);
        long useLimitTime = 24L;
        String couponStatus = "issuing";
        LocalDateTime regDate = LocalDateTime.parse(regDateStr, formatter);

        Coupon couponIssuing = new Coupon(couponId,couponName,discountPrice,totalCouponAmount,remainingCouponAmount,minUsePrice,issuanceStartTime,issuanceEndTime,useLimitTime,couponStatus,regDate);

        //When
        couponIssuing.validateCouponIssuance();
        couponIssuing.decreaseCoupon();

        //Then
        assertEquals(9L,couponIssuing.getRemainingCouponAmount());
    }
}