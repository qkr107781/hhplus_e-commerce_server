package kr.hhplus.be.server.integration.application.coupon;

import kr.hhplus.be.server.TestContainersConfiguration;
import kr.hhplus.be.server.application.coupon.dto.CouponResponse;
import kr.hhplus.be.server.application.coupon.service.CouponService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.*;
@Sql(scripts = "/coupon.sql",executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS) //테스트 실행 시 해당 .sql 파일내의 쿼리 실행 -> 테이블 생성 후 실행됨
@Sql(scripts = "/couponIssuedInfo.sql",executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS) //테스트 실행 시 해당 .sql 파일내의 쿼리 실행 -> 테이블 생성 후 실행됨
public class CouponServiceTest extends TestContainersConfiguration {

    @Autowired
    CouponService couponService;

    @Test
    @DisplayName("쿠폰 발급")
    void issuingCoupon() throws Exception {
        //Given
        long couponId = 3L;
        long userId = 1L;

        //When
        CouponResponse.Issue couponIssue = couponService.issuingCoupon(couponId,userId);

        //Then
        assertEquals("복귀 쿠폰",couponIssue.couponName());
    }

}
