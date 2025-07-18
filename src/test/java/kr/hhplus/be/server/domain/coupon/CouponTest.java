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
        Coupon remainingCouponEmpty = Coupon.builder()
                .remainingCouponAmount(0L)
                .build();

        //When
        boolean canIssue = remainingCouponEmpty.validateCouponIssuance(remainingCouponEmpty);

        //Then
        assertFalse(canIssue);
    }

    @Test
    @DisplayName("[쿠폰 발급][쿠폰 발급 가능 시간 아님]쿠폰 발급 시간 이전/이후 발급 요청하여 발급 실패")
    void notIssuingTime(){
        //Given
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String issuanceStartTimeStr = "2025-07-15 09:00:00";
        LocalDateTime issuanceStartTime = LocalDateTime.parse(issuanceStartTimeStr, formatter);

        String issuanceEndTimeStr = "2025-07-15 11:00:00";
        LocalDateTime issuanceEndTime = LocalDateTime.parse(issuanceEndTimeStr, formatter);

        Coupon notYetToIssueCoupon = Coupon.builder()
                .issuanceStartTime(issuanceStartTime)
                .issuanceEndTime(issuanceEndTime)
                .remainingCouponAmount(10L)
                .build();

        //When
        boolean canIssue = notYetToIssueCoupon.validateCouponIssuance(notYetToIssueCoupon);

        //Then
        assertFalse(canIssue);
    }

    @Test
    @DisplayName("[쿠폰 발급][정상 발급]쿠폰 발급 가능 시간이며 잔여 수량 충분하여 발급 성공")
    void canIssueCoupon(){
        //Given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime issuanceStartTime = now.minusHours(1);
        LocalDateTime issuanceEndTime = now.plusHours(1);

        Coupon canIssueCoupon = Coupon.builder()
                .remainingCouponAmount(10L)
                .issuanceStartTime(issuanceStartTime)
                .issuanceEndTime(issuanceEndTime)
                .build();

        //When
        boolean canIssue = canIssueCoupon.validateCouponIssuance(canIssueCoupon);

        //Then
        assertTrue(canIssue);
    }
}