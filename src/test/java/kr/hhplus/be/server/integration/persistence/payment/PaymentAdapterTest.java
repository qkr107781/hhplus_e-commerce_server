package kr.hhplus.be.server.integration.persistence.payment;

import kr.hhplus.be.server.TestContainersConfiguration;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.persistence.balance.BalanceAdapter;
import kr.hhplus.be.server.persistence.payment.PaymentAdapter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@Sql(scripts = "/payment.sql",executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS) //테스트 실행 시 해당 .sql 파일내의 쿼리 실행 -> 테이블 생성 후 실행됨
@ComponentScan(basePackageClasses = BalanceAdapter.class)//@Component 사용 중인 Adapter 클래스 읽어오기 위함
class PaymentAdapterTest extends TestContainersConfiguration {

    @Autowired
    PaymentAdapter paymentAdapter;

    @Test
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