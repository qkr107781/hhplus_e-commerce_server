package kr.hhplus.be.server.integration.persistence.coupon;

import kr.hhplus.be.server.TestContainersConfiguration;
import kr.hhplus.be.server.domain.coupon.CouponIssuedInfo;
import kr.hhplus.be.server.persistence.coupon.CouponIssuedInfoAdapter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // 테스트컨테이너에서 외부 DB 사용하도록 함
@ActiveProfiles("test") //application-test.yml 읽어오도록 함
@Sql(scripts = "/couponIssuedInfo.sql",executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS) //테스트 실행 시 해당 .sql 파일내의 쿼리 실행 -> 테이블 생성 후 실행됨
@ComponentScan(basePackageClasses = CouponIssuedInfoAdapter.class)//@Component 사용 중인 Adapter 클래스 읽어오기 위함
@ContextConfiguration(classes = TestContainersConfiguration.class)//Spring boot Context 로딩 전 TestContainerConfiguration 읽어오게 하기 위함
class CouponIssuedInfoJpaRepositoryTest {

    @Autowired
    CouponIssuedInfoAdapter couponIssuedInfoAdapter;

    @Test
    @Transactional
    @DisplayName("쿠폰 ID, 사용자 ID로 보유 쿠폰 발급 정보 조회- findByCouponIdAndUserId()")
    void findByCouponIdAndUserId(){
        System.out.println("findByCouponIdAndUserId 쿼리");
        //Given
        //사전 실행된 couponIssuedInfo.sql에서 데이터 입력했음
        //When
        CouponIssuedInfo couponIssuedInfo = couponIssuedInfoAdapter.findByCouponIdAndUserId(3L,1L);
        //Then
        assertEquals(3L,couponIssuedInfo.getCouponId());
        assertEquals(1L,couponIssuedInfo.getCouponIssuedId());
        assertEquals("N",couponIssuedInfo.getUseYn());
    }

    @Test
    @Transactional
//    @Commit
    @DisplayName("쿠폰 사용 처리 - save() - update")
    void useCoupon(){
        System.out.println("save update 쿼리");
        //Given
        //사전 실행된 couponIssuedInfo.sql에서 데이터 입력했음
        CouponIssuedInfo couponIssuedInfo = couponIssuedInfoAdapter.findByCouponIdAndUserId(3L,1L);
        //When
        couponIssuedInfo.useCoupon();
        couponIssuedInfo = couponIssuedInfoAdapter.useCoupon(couponIssuedInfo);
        //Then
        assertEquals(3L,couponIssuedInfo.getCouponId());
        assertEquals(1L,couponIssuedInfo.getCouponIssuedId());
        assertEquals("Y",couponIssuedInfo.getUseYn());
    }

    @Test
    @Transactional
//    @Commit
    @DisplayName("쿠폰 발급 - save() - insert")
    void issuingCoupon(){
        System.out.println("save insert 쿼리");
        //Given
        LocalDateTime issuedDateTime = LocalDateTime.now();
        CouponIssuedInfo couponIssuedInfo = CouponIssuedInfo.builder()
                .userId(1L)
                .useYn("N")
                .issuedAt(issuedDateTime)
                .endDate(issuedDateTime.plusDays(1))
                .couponId(2L)
                .build();
        //When
        CouponIssuedInfo afterIssuingCoupon = couponIssuedInfoAdapter.issuingCoupon(couponIssuedInfo);
        //Then
        assertEquals(2L,afterIssuingCoupon.getCouponId());
        assertEquals(2L,afterIssuingCoupon.getCouponIssuedId());
        assertEquals("N",afterIssuingCoupon.getUseYn());
        assertEquals(issuedDateTime,afterIssuingCoupon.getIssuedAt());
    }

    @Test
    @Transactional
    @DisplayName("사용자 ID로 소유 쿠폰 조회 - findByUserId()")
    void findByUserId(){
        System.out.println("findByUserId 쿼리");
        //Given
        //사전 실행된 couponIssuedInfo.sql에서 데이터 입력했음
        //When
        CouponIssuedInfo couponIssuedInfo = couponIssuedInfoAdapter.findByUserId(1L);
        //Then
        assertEquals(3L,couponIssuedInfo.getCouponId());
        assertEquals(1L,couponIssuedInfo.getCouponIssuedId());
        assertEquals("N",couponIssuedInfo.getUseYn());
    }
}