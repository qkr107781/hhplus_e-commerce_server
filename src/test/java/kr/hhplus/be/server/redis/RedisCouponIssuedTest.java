package kr.hhplus.be.server.redis;

import kr.hhplus.be.server.ServerApplication;
import kr.hhplus.be.server.TestContainersConfiguration;
import kr.hhplus.be.server.application.coupon.service.CouponService;
import kr.hhplus.be.server.common.redis.RedisKeys;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponIssuedInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.api.*;
import org.redisson.client.codec.StringCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {ServerApplication.class, TestContainersConfiguration.class})
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // 테스트컨테이너에서 외부 DB 사용하도록 함
public class RedisCouponIssuedTest {

    @Autowired
    CouponService couponService;

    @Autowired
    RedissonClient redissonClient;

    @Sql(scripts = "/coupon.sql",executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD) //테스트 실행 시 해당 .sql 파일내의 쿼리 실행 -> 테이블 생성 후 실행됨
    @Sql(scripts = "/delete.sql",executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD) //이 메소드 테스트 종료 시 데이터 클랜징
    @Test
    @DisplayName("레디스 자료구조 기반 선착순 쿠폰 발급")
    void redisCouponIssue() throws InterruptedException, BrokenBarrierException {
        //Given
        //파라미터 셋팅
        long couponId_5 = 5L; //->준비된 쿠폰은 100개
        long couponId_3 = 3L; //->준비된 쿠폰은 100개
        long couponId_4 = 4L; //->준비된 쿠폰은 100개

        //쿠폰 메타 정보 Hashes 세팅
        String key5 = RedisKeys.COUPON_META.format("5");
        RMap<String, String> couponMeta5 = redissonClient.getMap(key5, StringCodec.INSTANCE);

        // 3. Map에 값 추가
        couponMeta5.put("total_quantity", "100");
        couponMeta5.put("remain_quantity", "100");
        couponMeta5.put("start_date", LocalDateTime.now().toString());
        couponMeta5.put("end_date", LocalDateTime.now().plusDays(1).toString());
        couponMeta5.expire(Instant.now().plus(86400 + 3600, ChronoUnit.SECONDS));//TTL: 발급 종료일 - 발급 시작일 + 1시간

        //쿠폰 메타 정보 Hashes 세팅
        String key3 = RedisKeys.COUPON_META.format("3");
        RMap<String, String> couponMeta3 = redissonClient.getMap(key3, StringCodec.INSTANCE);

        // 3. Map에 값 추가
        couponMeta3.put("total_quantity", "100");
        couponMeta3.put("remain_quantity", "100");
        couponMeta3.put("start_date", LocalDateTime.now().toString());
        couponMeta3.put("end_date", LocalDateTime.now().plusDays(1).toString());
        couponMeta3.expire(Instant.now().plus(86400 + 3600, ChronoUnit.SECONDS));//TTL: 발급 종료일 - 발급 시작일 + 1시간

        //쿠폰 메타 정보 Hashes 세팅
        String key4 = RedisKeys.COUPON_META.format("4");
        RMap<String, String> couponMeta4 = redissonClient.getMap(key4, StringCodec.INSTANCE);

        // 3. Map에 값 추가
        couponMeta4.put("total_quantity", "100");
        couponMeta4.put("remain_quantity", "100");
        couponMeta4.put("start_date", LocalDateTime.now().toString());
        couponMeta4.put("end_date", LocalDateTime.now().plusDays(1).toString());
        couponMeta4.expire(Instant.now().plus(86400 + 3600, ChronoUnit.SECONDS));//TTL: 발급 종료일 - 발급 시작일 + 1시간

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
                    couponService.issuingCouponAsync(couponId_5, finalI);
                    couponService.issuingCouponAsync(couponId_4, finalI);
                    couponService.issuingCouponAsync(couponId_3, finalI);
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
        //발급 요청 Sets 확인
        String setsQueueKey = RedisKeys.COUPON_QUEUE.format("5");
        RSet<String> requestSet = redissonClient.getSet(setsQueueKey);
        System.out.println("쿠폰 5 발급 요청 Sets size: "+requestSet.size());
        String setsQueueKey4 = RedisKeys.COUPON_QUEUE.format("4");
        RSet<String> requestSet4 = redissonClient.getSet(setsQueueKey4);
        System.out.println("쿠폰 4 발급 요청 Sets size: "+requestSet4.size());
        String setsQueueKey3 = RedisKeys.COUPON_QUEUE.format("3");
        RSet<String> requestSet3 = redissonClient.getSet(setsQueueKey3);
        System.out.println("쿠폰 3 발급 요청 Sets size: "+requestSet3.size());

        //발급 작업 큐 Streams 확인
        String streamsQueueKey = RedisKeys.COUPON_ISSUE_JOB.format();
        RStream<String, String> queueStream = redissonClient.getStream(streamsQueueKey);
        System.out.println("발급 작업 큐 Streams size: "+queueStream.size());
//        // 0부터 ~ 끝까지 모든 메시지 가져오기
//        Map<StreamMessageId, Map<String, String>> entries = queueStream.range(
//                StreamMessageId.MIN, StreamMessageId.MAX
//        );
//
//        System.out.println("=== Stream 전체 데이터 ===");
//        for (Map.Entry<StreamMessageId, Map<String, String>> entry : entries.entrySet()) {
//            StreamMessageId id = entry.getKey();
//            Map<String, String> data = entry.getValue();
//            System.out.println("ID: " + id + ", Data: " + data);
//        }
//        System.out.println("=========================");

        // Work Thread 도는거 기다리려고 함
        while(true){
            RStream<String, String> queueStreamCheck = redissonClient.getStream(streamsQueueKey);
            if(queueStreamCheck.size() == 0){
                System.out.println("작업 종료 - 큐 스트림 전부 소진됨");
                break;
            }
        }

        System.out.println("발급 실패 건수: "+couponEmptyErrorCount);
        assertEquals(0L,couponEmptyErrorCount.get());

        //발급 종료 후 잔여 쿠폰 갯수 조회 -> 0개 예상
        Coupon coupon_3 = couponService.selectCouponByCouponId(couponId_3);
        //발급된 쿠폰 갯수 조회 -> 100건 조회 예상
        List<CouponIssuedInfo> resultList_3 = new ArrayList<>();
        for(int i = 0; i < threadCount; i++){
            CouponIssuedInfo result = couponService.selectCouponByCouponIdAndUserId(couponId_3,i);
            if(result != null){
                resultList_3.add(result);
            }
        }

        System.out.println("쿠폰-3 발급 후 잔여 쿠폰 갯수: "+coupon_3.getRemainingCouponAmount());
        System.out.println("쿠폰-3 총 쿠폰 발급 갯수: "+resultList_3.size());

        assertEquals(0L,coupon_3.getRemainingCouponAmount());
        assertEquals(100,resultList_3.size());

        //발급 종료 후 잔여 쿠폰 갯수 조회 -> 0개 예상
        Coupon coupon_4 = couponService.selectCouponByCouponId(couponId_4);
        //발급된 쿠폰 갯수 조회 -> 100건 조회 예상
        List<CouponIssuedInfo> resultList_4 = new ArrayList<>();
        for(int i = 0; i < threadCount; i++){
            CouponIssuedInfo result = couponService.selectCouponByCouponIdAndUserId(couponId_4,i);
            if(result != null){
                resultList_4.add(result);
            }
        }

        System.out.println("쿠폰-4 발급 후 잔여 쿠폰 갯수: "+coupon_4.getRemainingCouponAmount());
        System.out.println("쿠폰-4 총 쿠폰 발급 갯수: "+resultList_4.size());

        assertEquals(0L,coupon_4.getRemainingCouponAmount());
        assertEquals(100,resultList_4.size());

        //발급 종료 후 잔여 쿠폰 갯수 조회 -> 0개 예상
        Coupon coupon_5 = couponService.selectCouponByCouponId(couponId_5);
        //발급된 쿠폰 갯수 조회 -> 100건 조회 예상
        List<CouponIssuedInfo> resultList_5 = new ArrayList<>();
        for(int i = 0; i < threadCount; i++){
            CouponIssuedInfo result = couponService.selectCouponByCouponIdAndUserId(couponId_5,i);
            if(result != null){
                resultList_5.add(result);
            }
        }

        System.out.println("쿠폰-5 발급 후 잔여 쿠폰 갯수: "+coupon_5.getRemainingCouponAmount());
        System.out.println("쿠폰-5 총 쿠폰 발급 갯수: "+resultList_5.size());

        assertEquals(0L,coupon_5.getRemainingCouponAmount());
        assertEquals(100,resultList_5.size());
    }

}
