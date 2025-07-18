package kr.hhplus.be.server.domain.coupon;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CouponTest {

    private CouponIssuedInfo couponIssuedInfo;

    @BeforeEach
    void setUp() {
        Coupon coupon = Coupon.builder()
                .coupon_id(1L)
                .coupon_name("신규 가입 쿠폰")
                .issuance_start_time(LocalDateTime.now().minusHours(2))
                .issuance_end_time(LocalDateTime.now().minusHours(1))
                .remaining_coupon_amount(0L)
                .build();

        couponIssuedInfo = CouponIssuedInfo.builder()
                        .coupon_issued_id(1L)
                        .user_id(1L)
                        .coupon_id(1L)
                        .issued_at(LocalDateTime.now().minusHours(2))
                        .coupon(coupon)
                        .build();

    }

    @Test
    @DisplayName("[쿠폰 발급][잔여 쿠폰 0개]쿠폰이 모두 소진되어 발급 실패")
    void noCouponToIssue(){
        //Given
        //@BeforeEach에서 진행
        long user_id = 1L;
        //When
        boolean result = couponIssuedInfo.validateCoupon(couponIssuedInfo,user_id);
        //Then
        assertFalse(result);
    }

    @Test
    @DisplayName("[쿠폰 발급][쿠폰 발급 가능 시간 아님]쿠폰 발급 시간 이전/이후 발급 요청하여 발급 실패")
    void notIssuingTime(){
        //Given
        //@BeforeEach에서 진행
        long user_id = 1L;
        //When
        boolean result = couponIssuedInfo.validateCoupon(couponIssuedInfo,user_id);
        //Then
        assertFalse(result);
    }

    @Test
    @DisplayName("[쿠폰 발급][쿠폰 중복 발급 요청]이미 동일 쿠폰 발급 받아 추가 발급 실패")
    void dulicateedIssuing(){
        //Given
        //@BeforeEach에서 진행
        long user_id = 2L;
        //When
        boolean result = couponIssuedInfo.validateCoupon(couponIssuedInfo,user_id);
        //Then
        assertFalse(result);
    }

}