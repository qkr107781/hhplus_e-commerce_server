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
import java.util.concurrent.atomic.AtomicInteger;

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

    @Test
    @DisplayName("주문 생성 vs 주문 취소 동시성 테스트 - 재고 반복 차감")
    void orderCreateAndOrderCancel2() throws Exception {
        //Given
        //파라미터 셋팅
        long userId_create = 1L; //주문 생성 요청 유저
        //1: 1개, 2: 15개, 3~4: 3개씩
        List<Long> create_productOptionId_1 = List.of(1L,2L,2L,2L,2L,2L,2L,2L,2L,2L,2L,2L,2L,2L,2L,2L,3L,3L,3L,4L,4L,4L);
        long couponId_create_1 = 3L;
        OrderRequest.OrderCreate createOrder_1 = new OrderRequest.OrderCreate(userId_create,create_productOptionId_1,couponId_create_1);

        List<Long> create_productOptionId_2 = List.of(1L,2L,2L,2L,2L,2L,2L,2L,2L,2L,2L,2L,2L,2L,2L,2L,3L,3L,3L,4L,4L,4L);
        long couponId_create_2 = 4L;
        OrderRequest.OrderCreate createOrder_2 = new OrderRequest.OrderCreate(userId_create,create_productOptionId_2,couponId_create_2);

        List<Long> create_productOptionId_3 = List.of(1L,2L,2L,2L,2L,2L,2L,2L,2L,2L,2L,2L,2L,2L,2L,2L,3L,3L,3L,4L,4L,4L);
        long couponId_create_3 = 5L;
        OrderRequest.OrderCreate createOrder_3 = new OrderRequest.OrderCreate(userId_create,create_productOptionId_3,couponId_create_3);


        //동시성 테스트 준비
        int threadCount = 3;

        //모든 스레드가 동시에 시작할 수 있도록 돕는 Barrier
        //threadCount + 1 (메인 스레드)
        CyclicBarrier startBarrier = new CyclicBarrier(threadCount + 1);

        //모든 스레드가 작업을 완료했음을 알리는 Latch
        CountDownLatch endLatch = new CountDownLatch(threadCount);

        //Thread Pool 생성
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        AtomicInteger couponEmptyErrorCount_1 = new AtomicInteger();
        executor.submit(() -> {
            try {
                //모든 스레드가 Barrier에 도착할 때까지 대기
                startBarrier.await();

                //주문 생성
                orderFacadeService.createOrder(createOrder_1);
            } catch (Exception e) {
                System.err.println("스레드 실행 중 오류: " + e.getMessage());
                if(e.getMessage().contains("stock empty")){
                    couponEmptyErrorCount_1.getAndIncrement();
                }
            } finally {
                //이 스레드가 작업을 완료했음을 Latch에 알림
                endLatch.countDown();
            }
        });

        AtomicInteger couponEmptyErrorCount_2 = new AtomicInteger();
        executor.submit(() -> {
            try {
                //모든 스레드가 Barrier에 도착할 때까지 대기
                startBarrier.await();

                //주문 생성
                orderFacadeService.createOrder(createOrder_2);
            } catch (Exception e) {
                System.err.println("스레드 실행 중 오류: " + e.getMessage());
                if(e.getMessage().contains("stock empty")){
                    couponEmptyErrorCount_2.getAndIncrement();
                }
            } finally {
                //이 스레드가 작업을 완료했음을 Latch에 알림
                endLatch.countDown();
            }
        });

        AtomicInteger couponEmptyErrorCount_3 = new AtomicInteger();
        executor.submit(() -> {
            try {
                //모든 스레드가 Barrier에 도착할 때까지 대기
                startBarrier.await();

                //주문 생성
                orderFacadeService.createOrder(createOrder_3);
            } catch (Exception e) {
                System.err.println("스레드 실행 중 오류: " + e.getMessage());
                if(e.getMessage().contains("stock empty")){
                    couponEmptyErrorCount_3.getAndIncrement();
                }
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

        CouponIssuedInfo couponOrderCreate_1 = couponService.selectCouponByCouponIdAndUserId(couponId_create_1,userId_create);
        CouponIssuedInfo couponOrderCreate_2 = couponService.selectCouponByCouponIdAndUserId(couponId_create_2,userId_create);
        CouponIssuedInfo couponOrderCreate_3 = couponService.selectCouponByCouponIdAndUserId(couponId_create_3,userId_create);

        System.out.println("주문 생성 후 쿠폰 사용_1: "+couponOrderCreate_1.getUseYn());
        System.out.println("주문 생성 후 쿠폰 사용_2: "+couponOrderCreate_2.getUseYn());
        System.out.println("주문 생성 후 쿠폰 사용_3: "+couponOrderCreate_3.getUseYn());

        //각 스레드 실패 건수 확인
        System.out.println("1번 쓰레드 실패 건수: "+couponEmptyErrorCount_1.get());
        System.out.println("2번 쓰레드 실패 건수: "+couponEmptyErrorCount_2.get());
        System.out.println("3번 쓰레드 실패 건수: "+couponEmptyErrorCount_3.get());

        assertEquals(27L,productOption_1.getStockQuantity());
        assertEquals(0L,productOption_2.getStockQuantity());
        assertEquals(24L,productOption_3.getStockQuantity());
        assertEquals(24L,productOption_4.getStockQuantity());
        assertEquals(30L,productOption_5.getStockQuantity());
        assertEquals("Y",couponOrderCreate_1.getUseYn());
        assertEquals("Y",couponOrderCreate_2.getUseYn());
        assertEquals("Y",couponOrderCreate_3.getUseYn());
    }


    @Test
    @DisplayName("주문 생성 vs 주문 취소 동시성 테스트 - 재고 반복 복구")
    void orderCreateAndOrderCancel3() throws Exception {
        //Given
        //파라미터 셋팅
        long userId_cancel_1 = 2L; //주문 취소 요청 유저
        //2~4: 5개씩, 5: 1개
        OrderRequest.OrderCancel cancelOrder_1 = new OrderRequest.OrderCancel(userId_cancel_1,4L);

        long userId_cancel_2 = 2L; //주문 취소 요청 유저
        //2~4: 5개씩, 5: 1개
        OrderRequest.OrderCancel cancelOrder_2 = new OrderRequest.OrderCancel(userId_cancel_2,5L);

        long userId_cancel_3 = 2L; //주문 취소 요청 유저
        //2~4: 5개씩, 5: 1개
        OrderRequest.OrderCancel cancelOrder_3 = new OrderRequest.OrderCancel(userId_cancel_3,6L);

        long userId_cancel_4 = 2L; //주문 취소 요청 유저
        //2~4: 5개씩, 5: 1개
        OrderRequest.OrderCancel cancelOrder_4 = new OrderRequest.OrderCancel(userId_cancel_4,7L);

        long userId_cancel_5 = 2L; //주문 취소 요청 유저
        //2~4: 5개씩, 5: 1개
        OrderRequest.OrderCancel cancelOrder_5 = new OrderRequest.OrderCancel(userId_cancel_5,8L);

        //동시성 테스트 준비
        int threadCount = 5;

        //모든 스레드가 동시에 시작할 수 있도록 돕는 Barrier
        //threadCount + 1 (메인 스레드)
        CyclicBarrier startBarrier = new CyclicBarrier(threadCount + 1);

        //모든 스레드가 작업을 완료했음을 알리는 Latch
        CountDownLatch endLatch = new CountDownLatch(threadCount);

        //Thread Pool 생성
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        AtomicInteger couponEmptyErrorCount_1 = new AtomicInteger();
        executor.submit(() -> {
            try {
                //모든 스레드가 Barrier에 도착할 때까지 대기
                startBarrier.await();

                //주문 생성
                orderFacadeService.cancelOrder(cancelOrder_1);
            } catch (Exception e) {
                System.err.println("스레드 실행 중 오류: " + e.getMessage());
                if(e.getMessage().contains("restore stock over")){
                    couponEmptyErrorCount_1.getAndIncrement();
                }
            } finally {
                //이 스레드가 작업을 완료했음을 Latch에 알림
                endLatch.countDown();
            }
        });

        AtomicInteger couponEmptyErrorCount_2 = new AtomicInteger();
        executor.submit(() -> {
            try {
                //모든 스레드가 Barrier에 도착할 때까지 대기
                startBarrier.await();

                //주문 생성
                orderFacadeService.cancelOrder(cancelOrder_2);
            } catch (Exception e) {
                System.err.println("스레드 실행 중 오류: " + e.getMessage());
                if(e.getMessage().contains("restore stock over")){
                    couponEmptyErrorCount_2.getAndIncrement();
                }
            } finally {
                //이 스레드가 작업을 완료했음을 Latch에 알림
                endLatch.countDown();
            }
        });

        AtomicInteger couponEmptyErrorCount_3 = new AtomicInteger();
        executor.submit(() -> {
            try {
                //모든 스레드가 Barrier에 도착할 때까지 대기
                startBarrier.await();

                //주문 생성
                orderFacadeService.cancelOrder(cancelOrder_3);
            } catch (Exception e) {
                System.err.println("스레드 실행 중 오류: " + e.getMessage());
                if(e.getMessage().contains("restore stock over")){
                    couponEmptyErrorCount_3.getAndIncrement();
                }
            } finally {
                //이 스레드가 작업을 완료했음을 Latch에 알림
                endLatch.countDown();
            }
        });

        AtomicInteger couponEmptyErrorCount_4 = new AtomicInteger();
        executor.submit(() -> {
            try {
                //모든 스레드가 Barrier에 도착할 때까지 대기
                startBarrier.await();

                //주문 생성
                orderFacadeService.cancelOrder(cancelOrder_4);
            } catch (Exception e) {
                System.err.println("스레드 실행 중 오류: " + e.getMessage());
                if(e.getMessage().contains("restore stock over")){
                    couponEmptyErrorCount_4.getAndIncrement();
                }
            } finally {
                //이 스레드가 작업을 완료했음을 Latch에 알림
                endLatch.countDown();
            }
        });

        AtomicInteger couponEmptyErrorCount_5 = new AtomicInteger();
        executor.submit(() -> {
            try {
                //모든 스레드가 Barrier에 도착할 때까지 대기
                startBarrier.await();

                //주문 생성
                orderFacadeService.cancelOrder(cancelOrder_5);
            } catch (Exception e) {
                System.err.println("스레드 실행 중 오류: " + e.getMessage());
                if(e.getMessage().contains("restore stock over")){
                    couponEmptyErrorCount_5.getAndIncrement();
                }
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

        CouponIssuedInfo couponOrderCreate_1 = couponService.selectCouponByCouponIdAndUserId(4L,userId_cancel_1);
        CouponIssuedInfo couponOrderCreate_2 = couponService.selectCouponByCouponIdAndUserId(5L,userId_cancel_1);
        CouponIssuedInfo couponOrderCreate_3 = couponService.selectCouponByCouponIdAndUserId(6L,userId_cancel_1);
        CouponIssuedInfo couponOrderCreate_4 = couponService.selectCouponByCouponIdAndUserId(7L,userId_cancel_1);
        CouponIssuedInfo couponOrderCreate_5 = couponService.selectCouponByCouponIdAndUserId(8L,userId_cancel_1);

        System.out.println("주문 취소 후 쿠폰 사용_1: "+couponOrderCreate_1.getUseYn());
        System.out.println("주문 취소 후 쿠폰 사용_2: "+couponOrderCreate_2.getUseYn());
        System.out.println("주문 취소 후 쿠폰 사용_3: "+couponOrderCreate_3.getUseYn());
        System.out.println("주문 취소 후 쿠폰 사용_3: "+couponOrderCreate_4.getUseYn());
        System.out.println("주문 취소 후 쿠폰 사용_3: "+couponOrderCreate_5.getUseYn());

        //각 스레드 실패 건수 확인
        System.out.println("1번 쓰레드 실패 건수: "+couponEmptyErrorCount_1.get());
        System.out.println("2번 쓰레드 실패 건수: "+couponEmptyErrorCount_2.get());
        System.out.println("3번 쓰레드 실패 건수: "+couponEmptyErrorCount_3.get());
        System.out.println("4번 쓰레드 실패 건수: "+couponEmptyErrorCount_4.get());
        System.out.println("5번 쓰레드 실패 건수: "+couponEmptyErrorCount_5.get());

        assertEquals(30L,productOption_1.getStockQuantity());
        assertEquals(50L,productOption_2.getStockQuantity());
        assertEquals(50L,productOption_3.getStockQuantity());
        assertEquals(50L,productOption_4.getStockQuantity());
        assertEquals(34L,productOption_5.getStockQuantity());
        assertEquals("N",couponOrderCreate_1.getUseYn());
        assertEquals("N",couponOrderCreate_2.getUseYn());
        assertEquals("N",couponOrderCreate_3.getUseYn());
        assertEquals("N",couponOrderCreate_4.getUseYn());
        assertEquals("N",couponOrderCreate_5.getUseYn());
    }

}
