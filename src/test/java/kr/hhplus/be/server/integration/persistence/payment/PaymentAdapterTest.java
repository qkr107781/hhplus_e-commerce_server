package kr.hhplus.be.server.integration.persistence.payment;

import kr.hhplus.be.server.ServerApplication;
import kr.hhplus.be.server.TestContainersConfiguration;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.persistence.balance.BalanceAdapter;
import kr.hhplus.be.server.persistence.payment.PaymentAdapter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {ServerApplication.class, TestContainersConfiguration.class})
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // 테스트컨테이너에서 외부 DB 사용하도록 함
@Sql(scripts = "/payment.sql",executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS) //테스트 실행 시 해당 .sql 파일내의 쿼리 실행 -> 테이블 생성 후 실행됨
@Sql(scripts = "/delete.sql",executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS) //이 클래스 테스트 종료 시 데이터 클랜징
@ComponentScan(basePackageClasses = BalanceAdapter.class)//@Component 사용 중인 Adapter 클래스 읽어오기 위함
class PaymentAdapterTest {

    @Autowired
    PaymentAdapter paymentAdapter;

    @Test
    @Transactional
    @DisplayName("결제 저장 - save() - insert")
    void savePayment(){
        System.out.println("save insert 쿼리");
        //Given
        LocalDateTime paymentDate = LocalDateTime.now();
        Payment payment = Payment.builder()
                .userId(1L)
                .orderId(2L)
                .paymentPrice(100_000L)
                .paymentDate(paymentDate)
                .build();
        //When
        Payment afterPayment = paymentAdapter.save(payment);
        //Then
        assertEquals(2L,afterPayment.getPaymentId());
        assertEquals(1L,afterPayment.getUserId());
        assertEquals(2L,afterPayment.getOrderId());
        assertEquals(100_000L,afterPayment.getPaymentPrice());
        assertEquals(paymentDate,afterPayment.getPaymentDate());
    }

}