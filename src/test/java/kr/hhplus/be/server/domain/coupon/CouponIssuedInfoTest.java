package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.application.coupon.CouponIssuedInfoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CouponIssuedInfoTest {

    @Mock
    private CouponIssuedInfoRepository couponIssuedInfoRepository;

    @Test
    @DisplayName("[쿠폰 발급][쿠폰 중복 발급 요청]이미 동일 쿠폰 발급 받아 추가 발급 실패")
    void duplicatedIssuing(){
        //Given
        long couponId = 1L;
        long userId = 1L;

        //When
        when(couponIssuedInfoRepository.countByCouponIdAndUserId(couponId, userId)).thenReturn(1);

        //Then
        assertEquals(1, couponIssuedInfoRepository.countByCouponIdAndUserId(couponId, userId));
    }

}