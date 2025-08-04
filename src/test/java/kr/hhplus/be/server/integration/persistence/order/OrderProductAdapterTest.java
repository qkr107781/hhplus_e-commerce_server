package kr.hhplus.be.server.integration.persistence.order;

import kr.hhplus.be.server.TestContainersConfiguration;
import kr.hhplus.be.server.domain.order.OrderProduct;
import kr.hhplus.be.server.persistence.order.OrderProductAdapter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
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
@Sql(scripts = "/orderProduct.sql",executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS) //테스트 실행 시 해당 .sql 파일내의 쿼리 실행 -> 테이블 생성 후 실행됨
@Sql(scripts = "/delete.sql",executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS) //이 클래스 테스트 종료 시 데이터 클랜징
@ComponentScan(basePackageClasses = OrderProductAdapter.class)//@Component 사용 중인 Adapter 클래스 읽어오기 위함
@ContextConfiguration(classes = TestContainersConfiguration.class)//Spring boot Context 로딩 전 TestContainerConfiguration 읽어오게 하기 위함
class OrderProductAdapterTest {

    @Autowired
    OrderProductAdapter orderProductAdapter;

    @Test
    @Transactional
    @DisplayName("주문 상품 조회 - findByOrderId()")
    void findByOrderId(){
        System.out.println("findByOrderId 쿼리");
        //Given
        //사전 실행된 orderProduct.sql에서 데이터 입력했음
        //When
        List<OrderProduct> orderProductList = orderProductAdapter.findByOrderId(1L);
        //Then
        assertEquals(1L,orderProductList.get(0).getOrderProductId());
        assertEquals(1L,orderProductList.get(0).getProductId());
        assertEquals(1L,orderProductList.get(0).getProductOptionId());
        assertEquals(20_000L,orderProductList.get(0).getProductPrice());
        assertEquals(2L,orderProductList.get(0).getProductQuantity());

        assertEquals(2L,orderProductList.get(1).getOrderProductId());
        assertEquals(1L,orderProductList.get(1).getProductId());
        assertEquals(2L,orderProductList.get(1).getProductOptionId());
        assertEquals(10_000L,orderProductList.get(1).getProductPrice());
        assertEquals(1L,orderProductList.get(1).getProductQuantity());

        assertEquals(3L,orderProductList.get(2).getOrderProductId());
        assertEquals(2L,orderProductList.get(2).getProductId());
        assertEquals(4L,orderProductList.get(2).getProductOptionId());
        assertEquals(5_000L,orderProductList.get(2).getProductPrice());
        assertEquals(3L,orderProductList.get(2).getProductQuantity());
    }

    @Test
    @Transactional
//    @Commit
    @DisplayName("주문 상품 저장 - save() - insert")
    void saveInsert(){
        System.out.println("save - insert 쿼리");
        //Given
        OrderProduct orderProduct1 = OrderProduct.builder()
                .orderId(2L)
                .productId(3L)
                .productOptionId(1L)
                .productPrice(20_000L)
                .productQuantity(2L)
                .build();

        OrderProduct orderProduct2 = OrderProduct.builder()
                .orderId(2L)
                .productId(3L)
                .productOptionId(2L)
                .productPrice(10_000L)
                .productQuantity(1L)
                .build();

        OrderProduct orderProduct3 = OrderProduct.builder()
                .orderId(2L)
                .productId(4L)
                .productOptionId(1L)
                .productPrice(5_000L)
                .productQuantity(3L)
                .build();

        //When
        OrderProduct saveOrderProduct1 = orderProductAdapter.save(orderProduct1);
        OrderProduct saveOrderProduct2 = orderProductAdapter.save(orderProduct2);
        OrderProduct saveOrderProduct3 = orderProductAdapter.save(orderProduct3);

        //Then
        assertEquals(4L,saveOrderProduct1.getOrderProductId());
        assertEquals(2L,saveOrderProduct3.getOrderId());
        assertEquals(3L,saveOrderProduct1.getProductId());
        assertEquals(1L,saveOrderProduct1.getProductOptionId());
        assertEquals(20_000L,saveOrderProduct1.getProductPrice());
        assertEquals(2L,saveOrderProduct1.getProductQuantity());

        assertEquals(5L,saveOrderProduct2.getOrderProductId());
        assertEquals(2L,saveOrderProduct3.getOrderId());
        assertEquals(3L,saveOrderProduct2.getProductId());
        assertEquals(2L,saveOrderProduct2.getProductOptionId());
        assertEquals(10_000L,saveOrderProduct2.getProductPrice());
        assertEquals(1L,saveOrderProduct2.getProductQuantity());

        assertEquals(6L,saveOrderProduct3.getOrderProductId());
        assertEquals(2L,saveOrderProduct3.getOrderId());
        assertEquals(4L,saveOrderProduct3.getProductId());
        assertEquals(1L,saveOrderProduct3.getProductOptionId());
        assertEquals(5_000L,saveOrderProduct3.getProductPrice());
        assertEquals(3L,saveOrderProduct3.getProductQuantity());


    }

}