package kr.hhplus.be.server.concurrency;

import kr.hhplus.be.server.TestContainersConfiguration;
import kr.hhplus.be.server.application.coupon.service.CouponService;
import kr.hhplus.be.server.application.order.dto.OrderRequest;
import kr.hhplus.be.server.application.order.facade.OrderFacadeService;
import kr.hhplus.be.server.application.product.service.ProductService;
import kr.hhplus.be.server.domain.coupon.CouponIssuedInfo;
import kr.hhplus.be.server.domain.product.ProductOption;
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
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // 테스트컨테이너에서 외부 DB 사용하도록 함
@ActiveProfiles("test") //application-test.yml 읽어오도록 함
@Sql(scripts = "/concurrencyOrder.sql",executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS) //테스트 실행 시 해당 .sql 파일내의 쿼리 실행 -> 테이블 생성 후 실행됨
@Sql(scripts = "/delete.sql",executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS) //이 클래스 테스트 종료 시 데이터 클랜징
@ContextConfiguration(classes = TestContainersConfiguration.class)//Spring boot Context 로딩 전 TestContainerConfiguration 읽어오게 하기 위함
public class OrderCreateAndOrderCancelTest {

    @Autowired
    OrderFacadeService orderFacadeService;

    @Autowired
    ProductService productService;

    @Autowired
    CouponService couponService;

    @Test
    @DisplayName("주문 생성 vs 주문 취소 동시성 테스트")
    void orderCreateAndOrderCancel() throws Exception {
        //Given
        //파라미터 셋팅
        long userId_create = 1L; //주문 생성 요청 유저
        //1: 1개, 2~4: 3개씩
        List<Long> create_productOptionId = List.of(1L,2L,2L,2L,3L,3L,3L,4L,4L,4L);
        long couponId_create = 3L;
        OrderRequest.OrderCreate createOrder = new OrderRequest.OrderCreate(userId_create,create_productOptionId,couponId_create);


        long userId_cancel = 2L; //주문 취소 요청 유저
        //2~4: 5개씩, 5: 1개
        List<Long> cancel_productOptionId = List.of(2L,2L,2L,2L,2L,3L,3L,3L,3L,3L,4L,4L,4L,4L,4L,5L);
        long couponId_cancel = 3L;
        OrderRequest.OrderCancel cancelOrder = new OrderRequest.OrderCancel(userId_cancel,3L);


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

                //주문 생성
                orderFacadeService.createOrder(createOrder);
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

                //주문 취소
                orderFacadeService.cancelOrder(cancelOrder);
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
        //재고 확인
        ProductOption productOption_1 = productService.selectProductOptionByProductIdAndProductOptionId(1L,1L);
        ProductOption productOption_2 = productService.selectProductOptionByProductIdAndProductOptionId(1L,2L);
        ProductOption productOption_3 = productService.selectProductOptionByProductIdAndProductOptionId(1L,3L);
        ProductOption productOption_4 = productService.selectProductOptionByProductIdAndProductOptionId(1L,4L);
        ProductOption productOption_5 = productService.selectProductOptionByProductIdAndProductOptionId(1L,5L);

        System.out.println("옵션1 재고: "+productOption_1.getStockQuantity());
        System.out.println("옵션2 재고: "+productOption_2.getStockQuantity());
        System.out.println("옵션3 재고: "+productOption_3.getStockQuantity());
        System.out.println("옵션4 재고: "+productOption_4.getStockQuantity());
        System.out.println("옵션5 재고: "+productOption_5.getStockQuantity());

        CouponIssuedInfo couponOrderCreate = couponService.selectCouponByCouponIdAndUserId(couponId_create,userId_create);
        CouponIssuedInfo couponOrderCancel = couponService.selectCouponByCouponIdAndUserId(couponId_cancel,userId_cancel);

        System.out.println("주문 생성 후 쿠폰 사용: "+couponOrderCreate.getUseYn());
        System.out.println("주문 취소 후 쿠폰 복구: "+couponOrderCancel.getUseYn());

        assertEquals(29L,productOption_1.getStockQuantity());
        assertEquals(32L,productOption_2.getStockQuantity());
        assertEquals(32L,productOption_3.getStockQuantity());
        assertEquals(32L,productOption_4.getStockQuantity());
        assertEquals(31L,productOption_5.getStockQuantity());
        assertEquals("Y",couponOrderCreate.getUseYn());
        assertEquals("N",couponOrderCancel.getUseYn());
    }


}
