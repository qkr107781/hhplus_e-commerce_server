package kr.hhplus.be.server.integration.persistence.order;

import kr.hhplus.be.server.TestContainersConfiguration;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.persistence.order.OrderAdapter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Sql(scripts = "/order.sql",executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS) //테스트 실행 시 해당 .sql 파일내의 쿼리 실행 -> 테이블 생성 후 실행됨
@ComponentScan(basePackageClasses = OrderAdapter.class)//@Component 사용 중인 Adapter 클래스 읽어오기 위함
class OrderAdapterTest extends TestContainersConfiguration {

    @Autowired
    OrderAdapter orderAdapter;

    @Test
    @Transactional
//    @Commit
    @DisplayName("주문 저장 - save() - insert")
    void orderInsert(){
        System.out.println("save - insert 쿼리");
        //Given
        LocalDateTime orderDate = LocalDateTime.now();

        Order order = Order.builder()
                .userId(1L)
                .couponId(1L)
                .couponDiscountPrice(1_000L)
                .totalPrice(24_000L)
                .orderStatus("pending_payment")
                .orderDate(orderDate)
                .build();
        //When
        Order afterOrder = orderAdapter.save(order);
        //Then
        assertEquals(17L,afterOrder.getOrderId());
        assertEquals(1L,afterOrder.getUserId());
        assertEquals(orderDate,afterOrder.getOrderDate());
        assertEquals(24_000L,afterOrder.getTotalPrice());
    }

    @Test
    @DisplayName("주문 조회 - findByOrderId()")
    void findByOrderId(){
        System.out.println("findByOrderId 쿼리");
        //Given
        //사전 실행된 order.sql에서 데이터 입력했음
        //When
        Order order = orderAdapter.findByOrderId(1L);
        //Then
        assertEquals(1L,order.getOrderId());
        assertEquals(1L,order.getUserId());
        assertEquals(50_000L,order.getTotalPrice());
    }

    @Test
    @DisplayName("상품 통계 시 결제 완료된 추출 대상일 주문 조회 - findByOrderStatusAndOrderDateBetween()")
    void findByOrderStatusAndOrderDateBetween() {
        System.out.println("findByOrderStatusAndOrderDateBetween 쿼리");
        //Given
        //사전 실행된 order.sql에서 데이터 입력했음
        //When
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = LocalDate.now().minusDays(3);
        List<Order> orderList = orderAdapter.findByOrderStatusAndOrderDateBetween("complete_payment",startDate,endDate);
        //Then
        assertEquals(7,orderList.size());
    }

}