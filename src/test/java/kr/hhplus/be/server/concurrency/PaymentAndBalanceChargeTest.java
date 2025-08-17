package kr.hhplus.be.server.concurrency;

import kr.hhplus.be.server.TestContainersConfiguration;
import kr.hhplus.be.server.application.balance.dto.BalanceRequest;
import kr.hhplus.be.server.application.balance.dto.BalanceResponse;
import kr.hhplus.be.server.application.balance.service.BalanceService;
import kr.hhplus.be.server.application.payment.dto.PaymentRequest;
import kr.hhplus.be.server.application.payment.facade.PaymentFacadeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // 테스트컨테이너에서 외부 DB 사용하도록 함
@ActiveProfiles("test") //application-test.yml 읽어오도록 함
@Sql(scripts = "/concurrencyPaymentAndBalance.sql",executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS) //테스트 실행 시 해당 .sql 파일내의 쿼리 실행 -> 테이블 생성 후 실행됨
@Sql(scripts = "/delete.sql",executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS) //이 클래스 테스트 종료 시 데이터 클랜징
@ContextConfiguration(classes = TestContainersConfiguration.class)//Spring boot Context 로딩 전 TestContainerConfiguration 읽어오게 하기 위함
@EnableAspectJAutoProxy // AOP 활성화
public class PaymentAndBalanceChargeTest {

    @Autowired
    PaymentFacadeService paymentFacadeService;

    @Autowired
    BalanceService balanceService;

    //프록시 등록 상태 확인
    @Test
    void checkProxy() {
        System.out.println("Proxy balanceService? " + AopUtils.isAopProxy(balanceService));
        System.out.println("Proxy paymentFacadeService? " + AopUtils.isAopProxy(paymentFacadeService));
    }

    @Test
    @DisplayName("결제 vs 잔액 충전 동시성 테스트")
    void paymentAndBalanceCharge() throws Exception {
        //Given
        //파라미터 셋팅
        long userId = 1L;
        long orderId = 3L;
        PaymentRequest.Create createPayment = new PaymentRequest.Create(userId,orderId);

        long chargeAmount = 10_000L;
        BalanceRequest chargeBalance = new BalanceRequest(userId,chargeAmount);

        //동시성 테스트 준비
        int threadCount = 2;

        //모든 스레드가 동시에 시작할 수 있도록 돕는 Barrier
        //threadCount + 1 (메인 스레드)
        CyclicBarrier startBarrier = new CyclicBarrier(threadCount + 1);

        //모든 스레드가 작업을 완료했음을 알리는 Latch
        CountDownLatch endLatch = new CountDownLatch(threadCount);

        //Thread Pool 생성
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        executor.submit(() -> {
            try {
                //모든 스레드가 Barrier에 도착할 때까지 대기
                startBarrier.await();

                //결제
                paymentFacadeService.createPayment(createPayment);
            } catch (Exception e) {
                System.err.println("스레드 실행 중 오류: " + e.getMessage());
            } finally {
                //이 스레드가 작업을 완료했음을 Latch에 알림
                endLatch.countDown();
            }
        });

        executor.submit(() -> {
            try {
                //모든 스레드가 Barrier에 도착할 때까지 대기
                startBarrier.await();

                //잔액 충전
                balanceService.charge(chargeBalance);
            } catch (Exception e) {
                System.err.println("스레드 실행 중 오류: " + e.getMessage());
            } finally {
                //이 스레드가 작업을 완료했음을 Latch에 알림
                endLatch.countDown();
            }
        });

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
        BalanceResponse result = balanceService.selectBalanceByUserId(userId);
        System.out.println("사용자 잔액: "+result.balance());

        assertEquals(91_000L,result.balance());
    }


}
