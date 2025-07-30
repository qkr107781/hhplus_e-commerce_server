package kr.hhplus.be.server.domain.coupon;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CouponIssuedInfoTest {

    @Test
    @DisplayName("[쿠폰 사용][유효기간 초과]쿠폰 사용 가능한 유효기간 초과")
    void notIssuingTime(){
        //Given
        long totalOrderPrice = 24_000L;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String regDateStr = "2025-07-15 11:00:00";

        long couponId = 1L;
        String couponName = "신규 가입 쿠폰";
        long discountPrice = 1_000L;
        long totalCouponAmount = 30L;
        long remainingCouponAmount = 10L;
        LocalDateTime issuanceStartTime = LocalDateTime.now().minusHours(26);
        LocalDateTime issuanceEndTime = LocalDateTime.now().plusHours(24);
        String couponStatus = "issuing";
        LocalDateTime regDate = LocalDateTime.parse(regDateStr, formatter);

        Coupon coupon = Coupon.builder()
                .couponId(couponId)
                .couponName(couponName)
                .discountPrice(discountPrice)
                .totalCouponAmount(totalCouponAmount)
                .remainingCouponAmount(remainingCouponAmount)
                .issuanceStartTime(issuanceStartTime)
                .issuanceEndTime(issuanceEndTime)
                .couponStatus(couponStatus)
                .regDate(regDate)
                .build();

        long couponIssuedId = 1L;
        long userId = 1L;
        String useYn = "N";
        LocalDateTime issuedAt = LocalDateTime.now().minusHours(25);
        LocalDateTime endDAte = LocalDateTime.now().minusHours(1);

        CouponIssuedInfo couponIssuedInfo = new CouponIssuedInfo(couponIssuedId,userId,useYn,issuedAt,endDAte, coupon.getCouponId());

        //When
        boolean result = couponIssuedInfo.validateCouponUsage(totalOrderPrice, coupon.getDiscountPrice());

        //Then
        assertFalse(result);

    }

    @Test
    @DisplayName("[쿠폰 사용][주문 금액 초과]쿠폰 할인금액이 주문금액을 초과")
    void notIssuingStatus(){
        //Given
        long totalOrderPrice = 12_000L;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String regDateStr = "2025-07-15 11:00:00";

        long couponId = 1L;
        String couponName = "신규 가입 쿠폰";
        long discountPrice = 20_000L;
        long totalCouponAmount = 30L;
        long remainingCouponAmount = 10L;
        LocalDateTime issuanceStartTime = LocalDateTime.now().minusHours(1);
        LocalDateTime issuanceEndTime = LocalDateTime.now().plusHours(1);
        String couponStatus = "issuing";
        LocalDateTime regDate = LocalDateTime.parse(regDateStr, formatter);

        Coupon coupon = Coupon.builder()
                .couponId(couponId)
                .couponName(couponName)
                .discountPrice(discountPrice)
                .totalCouponAmount(totalCouponAmount)
                .remainingCouponAmount(remainingCouponAmount)
                .issuanceStartTime(issuanceStartTime)
                .issuanceEndTime(issuanceEndTime)
                .couponStatus(couponStatus)
                .regDate(regDate)
                .build();

        long couponIssuedId = 1L;
        long userId = 1L;
        String useYn = "N";
        LocalDateTime issuedAt = LocalDateTime.now();
        LocalDateTime endDAte = LocalDateTime.now().plusHours(24);

        CouponIssuedInfo couponIssuedInfo = new CouponIssuedInfo(couponIssuedId,userId,useYn,issuedAt,endDAte, coupon.getCouponId());

        //When
        boolean result = couponIssuedInfo.validateCouponUsage(totalOrderPrice,coupon.getDiscountPrice());

        //Then
        assertFalse(result);

    }

    @Test
    @DisplayName("[쿠폰 사용][사용 처리]쿠폰 사용 처리")
    void canIssueCoupon() throws Exception {
        //Given
        long totalOrderPrice = 24_000L;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String regDateStr = "2025-07-15 11:00:00";

        long couponId = 1L;
        String couponName = "신규 가입 쿠폰";
        long discountPrice = 1_000L;
        long totalCouponAmount = 30L;
        long remainingCouponAmount = 10L;
        LocalDateTime issuanceStartTime = LocalDateTime.now().minusHours(1);
        LocalDateTime issuanceEndTime = LocalDateTime.now().plusHours(1);
        String couponStatus = "issuing";
        LocalDateTime regDate = LocalDateTime.parse(regDateStr, formatter);

        Coupon coupon = Coupon.builder()
                .couponId(couponId)
                .couponName(couponName)
                .discountPrice(discountPrice)
                .totalCouponAmount(totalCouponAmount)
                .remainingCouponAmount(remainingCouponAmount)
                .issuanceStartTime(issuanceStartTime)
                .issuanceEndTime(issuanceEndTime)
                .couponStatus(couponStatus)
                .regDate(regDate)
                .build();

        long couponIssuedId = 1L;
        long userId = 1L;
        String useYn = "N";
        LocalDateTime issuedAt = LocalDateTime.now();
        LocalDateTime endDAte = LocalDateTime.now().plusHours(24);

        CouponIssuedInfo couponIssuedInfo = new CouponIssuedInfo(couponIssuedId,userId,useYn,issuedAt,endDAte, coupon.getCouponId());
        //When
        boolean result = couponIssuedInfo.validateCouponUsage(totalOrderPrice,coupon.getDiscountPrice());


        //Then
        assertTrue(result);
    }
}