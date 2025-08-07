package kr.hhplus.be.server.concurrency;

import kr.hhplus.be.server.TestContainersConfiguration;
import kr.hhplus.be.server.application.coupon.service.CouponService;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponIssuedInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // 테스트컨테이너에서 외부 DB 사용하도록 함
@ActiveProfiles("test") //application-test.yml 읽어오도록 함
@Sql(scripts = "/coupon.sql",executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS) //테스트 실행 시 해당 .sql 파일내의 쿼리 실행 -> 테이블 생성 후 실행됨
@Sql(scripts = "/delete.sql",executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS) //이 클래스 테스트 종료 시 데이터 클랜징
@ContextConfiguration(classes = TestContainersConfiguration.class)//Spring boot Context 로딩 전 TestContainerConfiguration 읽어오게 하기 위함
public class CouponIssueTest {

    @Autowired
    CouponService couponService;

    @Test
    @DisplayName("선착순 쿠폰 발급 동시성 테스트")
    void couponIssue() throws Exception {
    //Given
        //파라미터 셋팅
        long couponId = 5L; //->준비된 쿠폰은 100개

        //동시성 테스트 준비
        int threadCount = 200; //->threadCount로 userId 대체 -> 200명의 유저가 발급 요청

        //모든 스레드가 동시에 시작할 수 있도록 돕는 Barrier
        //threadCount + 1 (메인 스레드)
        CyclicBarrier startBarrier = new CyclicBarrier(threadCount + 1);

        //모든 스레드가 작업을 완료했음을 알리는 Latch
        CountDownLatch endLatch = new CountDownLatch(threadCount);

        //Thread Pool 생성
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        AtomicInteger couponEmptyErrorCount = new AtomicInteger();
        for (int i = 0; i < threadCount; i++) {
            int finalI = i;
            executor.submit(() -> {
                try {
                    //모든 스레드가 Barrier에 도착할 때까지 대기
                    startBarrier.await();

                    //쿠폰 발급
                    couponService.issuingCoupon(couponId, finalI);
                } catch (Exception e) {
                    System.err.println("스레드 실행 중 오류: " + e.getMessage());
                    if(e.getMessage().contains("empty remaining coupon")){
                        couponEmptyErrorCount.getAndIncrement();
                    }
                } finally {
                    //이 스레드가 작업을 완료했음을 Latch에 알림
                    endLatch.countDown();
                }
            });
        }

    //When
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

        //모든 스레드가 준비될 때까지 대기
        startBarrier.await();
        LocalDateTime startTime = LocalDateTime.now();
        System.out.println("대기 끝! 모든 스레드 실행 시작: " + startTime.format(formatter));
        //모든 스레드가 완료될 때까지 대기
        endLatch.await();

        //종료 처리
        executor.shutdown();
        LocalDateTime endTime = LocalDateTime.now();
        System.out.println("모든 스레드 실행 종료: " + endTime.format(formatter));

    //Then
        //발급 종료 후 잔여 쿠폰 갯수 조회 -> 0개 예상
        Coupon coupon = couponService.selectCouponByCouponId(couponId);
        //발급된 쿠폰 갯수 조회 -> 100건 조회 예상
        List<CouponIssuedInfo> resultList = new ArrayList<>();
        for(int i = 0; i < threadCount; i++){
            CouponIssuedInfo result = couponService.selectCouponByCouponIdAndUserId(couponId,i);
            if(result != null){
                resultList.add(result);
            }
        }

        System.out.println("발급 실패 건수: "+couponEmptyErrorCount);
        System.out.println("발급 후 잔여 쿠폰 갯수: "+coupon.getRemainingCouponAmount());
        System.out.println("총 쿠폰 발급 갯수: "+resultList.size());

        assertEquals(0L,coupon.getRemainingCouponAmount());
        assertEquals(100,resultList.size());
    }

}
