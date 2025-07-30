package kr.hhplus.be.server.application.coupon.service;

import kr.hhplus.be.server.application.coupon.dto.CouponResponse;
import kr.hhplus.be.server.application.coupon.repository.CouponIssuedInfoRepository;
import kr.hhplus.be.server.application.coupon.repository.CouponRepository;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponIssuedInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

    @Mock
    CouponRepository couponRepository;

    @Mock
    CouponIssuedInfoRepository couponIssuedInfoRepository;

    @Test
    @DisplayName("[쿠폰 발급]쿠폰 유효성 검증 후 발급 처리")
    void issuingCoupon() throws Exception {
        //Given
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

        CouponIssuedInfo couponIssuedInfo = CouponIssuedInfo.builder()
                .couponIssuedId(couponIssuedId)
                .userId(userId)
                .useYn(useYn)
                .issuedAt(issuedAt)
                .endDate(endDAte)
                .couponId(coupon.getCouponId())
                .build();

        when(couponRepository.findByCouponId(couponId)).thenReturn(coupon);
        when(couponIssuedInfoRepository.issuingCoupon(any(CouponIssuedInfo.class))).thenReturn(couponIssuedInfo);

        //When
        CouponService couponService = new CouponService(couponIssuedInfoRepository,couponRepository);
        CouponResponse.Issue result = couponService.issuingCoupon(couponId,userId);

        //Then
        assertEquals(9L,coupon.getRemainingCouponAmount());

        assertEquals("신규 가입 쿠폰", result.couponName());

        verify(couponRepository, times(1)).findByCouponId(couponId);
        verify(couponIssuedInfoRepository, times(1)).issuingCoupon(any(CouponIssuedInfo.class));
    }

    @Test
    @DisplayName("[쿠폰 사용]쿠폰 유효성 검증 후 사용 처리")
    void useCoupon() {
        //Given
        long totalOrderPrice = 24_000L;

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

        CouponIssuedInfo couponIssuedInfo = CouponIssuedInfo.builder()
                                                            .couponIssuedId(couponIssuedId)
                                                            .userId(userId)
                                                            .useYn(useYn)
                                                            .issuedAt(issuedAt)
                                                            .endDate(endDAte)
                                                            .couponId(coupon.getCouponId())
                                                            .build();

        when((couponRepository.findByCouponId(couponId))).thenReturn(coupon);
        when(couponIssuedInfoRepository.findByCouponIdAndUserId(couponId,userId)).thenReturn(couponIssuedInfo);
        when(couponIssuedInfoRepository.useCoupon(any(CouponIssuedInfo.class))).thenAnswer(invocation -> {
            CouponIssuedInfo info = invocation.getArgument(0);
            info.useCoupon();
            return info;
        });

        //When
        CouponService couponService = new CouponService(couponIssuedInfoRepository,couponRepository);
        CouponIssuedInfo result = couponService.useCoupon(couponId,userId,totalOrderPrice);

        //Then
        assertEquals("Y", result.getUseYn());

        verify(couponIssuedInfoRepository, times(1)).findByCouponIdAndUserId(couponId,userId);
        verify(couponIssuedInfoRepository, times(1)).useCoupon(any(CouponIssuedInfo.class));
    }

}