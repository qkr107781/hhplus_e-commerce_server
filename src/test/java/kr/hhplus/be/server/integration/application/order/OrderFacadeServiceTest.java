package kr.hhplus.be.server.integration.application.order;


import kr.hhplus.be.server.TestContainersConfiguration;
import kr.hhplus.be.server.application.order.dto.OrderRequest;
import kr.hhplus.be.server.application.order.dto.OrderResponse;
import kr.hhplus.be.server.application.order.facade.OrderFacadeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // 테스트컨테이너에서 외부 DB 사용하도록 함
@ActiveProfiles("test") //application-test.yml 읽어오도록 함
@Sql(scripts = "/coupon.sql",executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS) //테스트 실행 시 해당 .sql 파일내의 쿼리 실행 -> 테이블 생성 후 실행됨
@Sql(scripts = "/couponIssuedInfo.sql",executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS) //테스트 실행 시 해당 .sql 파일내의 쿼리 실행 -> 테이블 생성 후 실행됨
@Sql(scripts = "/product.sql",executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS) //테스트 실행 시 해당 .sql 파일내의 쿼리 실행 -> 테이블 생성 후 실행됨
@Sql(scripts = "/productOption.sql",executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS) //테스트 실행 시 해당 .sql 파일내의 쿼리 실행 -> 테이블 생성 후 실행됨
@Sql(scripts = "/delete.sql",executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS) //이 클래스 테스트 종료 시 데이터 클랜징
@ContextConfiguration(classes = TestContainersConfiguration.class)//Spring boot Context 로딩 전 TestContainerConfiguration 읽어오게 하기 위함
public class OrderFacadeServiceTest {

    @Autowired
    OrderFacadeService orderFacadeService;

    @Test
    @Transactional
//    @Commit
    @DisplayName("주문")
    void order() throws Exception {
        //Given
        OrderRequest.OrderCreate orderCreate = new OrderRequest.OrderCreate(1L, List.of(1L,1L,2L,3L,3L,3L),3L);

        //When
        OrderResponse.OrderCreate createResponse = orderFacadeService.createOrder(orderCreate);

        //Then
        //사용 쿠폰 확인
        assertEquals("복귀 쿠폰",createResponse.couponName());
        assertEquals(1_000L,createResponse.couponDiscountPrice());

        //주문 확인
        assertEquals(130_000L,createResponse.totalPrice());

        //주문 상품 확인
        assertEquals(2L,createResponse.orderProduct().get(0).productQuantity());
        assertEquals(1L,createResponse.orderProduct().get(1).productQuantity());
        assertEquals(3L,createResponse.orderProduct().get(2).productQuantity());
    }

}
