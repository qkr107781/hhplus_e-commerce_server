package kr.hhplus.be.server.redis;

import kr.hhplus.be.server.ServerApplication;
import kr.hhplus.be.server.TestContainersConfiguration;
import kr.hhplus.be.server.application.payment.dto.PaymentRequest;
import kr.hhplus.be.server.application.payment.dto.PaymentResponse;
import kr.hhplus.be.server.application.payment.facade.PaymentFacadeService;
import kr.hhplus.be.server.application.product.dto.ProductResponse;
import kr.hhplus.be.server.application.product.facade.ProductFacadeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.LongCodec;
import org.redisson.client.protocol.ScoredEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {ServerApplication.class, TestContainersConfiguration.class})
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // 테스트컨테이너에서 외부 DB 사용하도록 함
public class RedisTop5SalesProductTest {

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    ProductFacadeService productFacadeService;

    @Autowired
    PaymentFacadeService paymentFacadeService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final String DAILY_SALES_PREFIX = "daily:sales:";

    @Sql(scripts = "/order.sql",executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD) //테스트 실행 시 해당 .sql 파일내의 쿼리 실행 -> 테이블 생성 후 실행됨
    @Sql(scripts = "/orderProduct.sql",executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD) //테스트 실행 시 해당 .sql 파일내의 쿼리 실행 -> 테이블 생성 후 실행됨
    @Sql(scripts = "/balance.sql",executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD) //테스트 실행 시 해당 .sql 파일내의 쿼리 실행 -> 테이블 생성 후 실행됨
    @Sql(scripts = "/coupon.sql",executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD) //테스트 실행 시 해당 .sql 파일내의 쿼리 실행 -> 테이블 생성 후 실행됨
    @Sql(scripts = "/couponIssuedInfo.sql",executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD) //테스트 실행 시 해당 .sql 파일내의 쿼리 실행 -> 테이블 생성 후 실행됨
    @Sql(scripts = "/product.sql",executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD) //테스트 실행 시 해당 .sql 파일내의 쿼리 실행 -> 테이블 생성 후 실행됨
    @Sql(scripts = "/productOption.sql",executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD) //테스트 실행 시 해당 .sql 파일내의 쿼리 실행 -> 테이블 생성 후 실행됨
    @Sql(scripts = "/delete.sql",executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD) //이 클래스 테스트 종료 시 데이터 클랜징
    @Test
    @DisplayName("매 결제 시 상품옵션 별 판매 수량 레디스로 전송")
    void paymentProductOptionIdSendToRedis() throws Exception {
        //Given
        PaymentRequest.Create create = new PaymentRequest.Create(1L,1L);

        //When
        PaymentResponse.Create result = paymentFacadeService.createPayment(create);

        //Then
        assertEquals(49_000L,result.paymentPrice());
        assertEquals(50_000L,result.order().totalPrice());

        String redisKey = DAILY_SALES_PREFIX + LocalDate.now().format(DATE_FORMATTER);
        RScoredSortedSet<Long> sortedSet = redissonClient.getScoredSortedSet(redisKey, new LongCodec());
        LinkedList<Long> productOptionIdList = new LinkedList<>();
        LinkedList<Double> scoreList = new LinkedList<>();
        Iterator<ScoredEntry<Long>> sortedSetIterator = sortedSet.entryIterator();
        while(sortedSetIterator.hasNext()){
            ScoredEntry<Long> member = sortedSetIterator.next();
            productOptionIdList.add(member.getValue());
            scoreList.add(sortedSet.getScore(member.getValue()));
        }

        assertEquals(2L,productOptionIdList.get(0));
        assertEquals(1L,productOptionIdList.get(1));
        assertEquals(4L,productOptionIdList.get(2));

        assertEquals(1.0,scoreList.get(0));
        assertEquals(2.0,scoreList.get(1));
        assertEquals(3.0,scoreList.get(2));
    }

    @Sql(scripts = "/product.sql",executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD) //테스트 실행 시 해당 .sql 파일내의 쿼리 실행 -> 테이블 생성 후 실행됨
    @Sql(scripts = "/productOption.sql",executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD) //테스트 실행 시 해당 .sql 파일내의 쿼리 실행 -> 테이블 생성 후 실행됨
    @Sql(scripts = "/delete.sql",executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD) //이 클래스 테스트 종료 시 데이터 클랜징
    @Test
    @DisplayName("레디스 자료구조를 활용한 지난 3일간 인기 TOP5 상품 조회")
    void redisTop5SalseProduct() throws InterruptedException {
        //Given
        String redisKey_3_DaysAgo = DAILY_SALES_PREFIX + LocalDate.now().minusDays(3).format(DATE_FORMATTER);
        String redisKey_2_DaysAgo = DAILY_SALES_PREFIX + LocalDate.now().minusDays(2).format(DATE_FORMATTER);
        String redisKey_1_DaysAgo = DAILY_SALES_PREFIX + LocalDate.now().minusDays(1).format(DATE_FORMATTER);

        LinkedList<Long> productOptionIdList_3_DaysAgo = new LinkedList<>();
        productOptionIdList_3_DaysAgo.add(1L);
        productOptionIdList_3_DaysAgo.add(2L);
        productOptionIdList_3_DaysAgo.add(3L);
        productOptionIdList_3_DaysAgo.add(4L);
        productOptionIdList_3_DaysAgo.add(5L);

        LinkedList<Long> productOptionIdList_2_DaysAgo = new LinkedList<>();
        productOptionIdList_2_DaysAgo.add(3L);
        productOptionIdList_2_DaysAgo.add(4L);
        productOptionIdList_2_DaysAgo.add(5L);
        productOptionIdList_2_DaysAgo.add(6L);
        productOptionIdList_2_DaysAgo.add(7L);

        LinkedList<Long> productOptionIdList_1_DaysAgo = new LinkedList<>();
        productOptionIdList_1_DaysAgo.add(6L);
        productOptionIdList_1_DaysAgo.add(7L);
        productOptionIdList_1_DaysAgo.add(8L);
        productOptionIdList_1_DaysAgo.add(9L);
        productOptionIdList_1_DaysAgo.add(10L);

        LinkedList<Double> scoreList_3_DaysAgo = new LinkedList<>();
        scoreList_3_DaysAgo.add(55.0);
        scoreList_3_DaysAgo.add(40.0);
        scoreList_3_DaysAgo.add(30.0);
        scoreList_3_DaysAgo.add(20.0);
        scoreList_3_DaysAgo.add(10.0);

        LinkedList<Double> scoreList_2_DaysAgo = new LinkedList<>();
        scoreList_2_DaysAgo.add(50.0);
        scoreList_2_DaysAgo.add(40.0);
        scoreList_2_DaysAgo.add(30.0);
        scoreList_2_DaysAgo.add(20.0);
        scoreList_2_DaysAgo.add(10.0);

        LinkedList<Double> scoreList_1_DaysAgo = new LinkedList<>();
        scoreList_1_DaysAgo.add(50.0);
        scoreList_1_DaysAgo.add(40.0);
        scoreList_1_DaysAgo.add(30.0);
        scoreList_1_DaysAgo.add(20.0);
        scoreList_1_DaysAgo.add(10.0);

//        1: 55
//        2: 40
//        3: 80
//        4: 60
//        5: 40
//        6: 70
//        7: 50
//        8: 30
//        9: 20
//        10: 10

        //1위: 3L 80개 티셔츠1L-옵션3
        //2위: 6L 70개 반바지2L-옵션6
        //3위: 4L 60개 반바지2L-옵션4
        //4위: 1L 55개 티셔츠1L-옵션1
        //5위: 7L 50개 반바지2L-옵션7

        RScoredSortedSet<Long> sortedSet_3_DaysAgo = redissonClient.getScoredSortedSet(redisKey_3_DaysAgo, new LongCodec());
        RScoredSortedSet<Long> sortedSet_2_DaysAgo = redissonClient.getScoredSortedSet(redisKey_2_DaysAgo, new LongCodec());
        RScoredSortedSet<Long> sortedSet_1_DaysAgo = redissonClient.getScoredSortedSet(redisKey_1_DaysAgo, new LongCodec());

        for (int i = 0; i < productOptionIdList_3_DaysAgo.size(); i++) {
            long productOptionId = productOptionIdList_3_DaysAgo.get(i);
            double score = scoreList_3_DaysAgo.get(i);
            sortedSet_3_DaysAgo.add(score, productOptionId);
        }
        sortedSet_3_DaysAgo.expire((86400 * 3) + 3600, TimeUnit.SECONDS);//TTL: 3일 + 1시간 보관 후 삭제

        for (int i = 0; i < productOptionIdList_2_DaysAgo.size(); i++) {
            long productOptionId = productOptionIdList_2_DaysAgo.get(i);
            double score = scoreList_2_DaysAgo.get(i);
            sortedSet_2_DaysAgo.add(score, productOptionId);
        }
        sortedSet_2_DaysAgo.expire((86400 * 3) + 3600, TimeUnit.SECONDS);//TTL: 3일 + 1시간 보관 후 삭제

        for (int i = 0; i < productOptionIdList_1_DaysAgo.size(); i++) {
            long productOptionId = productOptionIdList_1_DaysAgo.get(i);
            double score = scoreList_1_DaysAgo.get(i);
            sortedSet_1_DaysAgo.add(score, productOptionId);
        }
        sortedSet_1_DaysAgo.expire((86400 * 3) + 3600, TimeUnit.SECONDS);//TTL: 3일 + 1시간 보관 후 삭제

        //When
        List<ProductResponse.Statistics> redisStatisticsList = productFacadeService.getTop5ForLast3Days();

        //Then
        assertEquals("티셔츠1L-옵션3",redisStatisticsList.get(0).productName());
        assertEquals("반바지2L-옵션6",redisStatisticsList.get(1).productName());
        assertEquals("반바지2L-옵션4",redisStatisticsList.get(2).productName());
        assertEquals("티셔츠1L-옵션1",redisStatisticsList.get(3).productName());
        assertEquals("반바지2L-옵션7",redisStatisticsList.get(4).productName());

        assertEquals(80,redisStatisticsList.get(0).salesQuantity());
        assertEquals(70,redisStatisticsList.get(1).salesQuantity());
        assertEquals(60,redisStatisticsList.get(2).salesQuantity());
        assertEquals(55,redisStatisticsList.get(3).salesQuantity());
        assertEquals(50,redisStatisticsList.get(4).salesQuantity());
    }

}
