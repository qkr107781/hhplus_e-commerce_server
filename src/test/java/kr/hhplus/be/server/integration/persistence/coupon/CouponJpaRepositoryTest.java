package kr.hhplus.be.server.integration.persistence.coupon;

import kr.hhplus.be.server.TestContainersConfiguration;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.persistence.coupon.CouponAdapter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Sql(scripts = "/coupon.sql",executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS) //테스트 실행 시 해당 .sql 파일내의 쿼리 실행 -> 테이블 생성 후 실행됨
@ComponentScan(basePackageClasses = CouponAdapter.class)//@Component 사용 중인 Adapter 클래스 읽어오기 위함
class CouponJpaRepositoryTest extends TestContainersConfiguration {

    @Autowired
    CouponAdapter couponAdapter;

    @Test
    @DisplayName("쿠폰 ID로 쿠폰정보 조회- findByCouponId()")
    void findByCouponId(){
        System.out.println("findByCouponId 쿼리");
        //Given
        //사전 실행된 coupon.sql에서 데이터 입력했음
        //When
        Coupon coupon = couponAdapter.findByCouponId(1L);
        //Then
        assertEquals("여름 쿠폰",coupon.getCouponName());
    }

    @Test
    @DisplayName("쿠폰 상태별 조회- findByCouponStatus()")
    void findByCouponStatus(){
        System.out.println("findByCouponStatus 쿼리");
        //Given
        //사전 실행된 coupon.sql에서 데이터 입력했음
        //When
        List<Coupon> couponList = couponAdapter.findByCouponStatus("issuing");
        //Then
        assertEquals("복귀 쿠폰",couponList.get(0).getCouponName());
    }
}