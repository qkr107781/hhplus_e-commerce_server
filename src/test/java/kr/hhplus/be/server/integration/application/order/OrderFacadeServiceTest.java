package kr.hhplus.be.server.integration.application.order;


import kr.hhplus.be.server.TestContainersConfiguration;
import kr.hhplus.be.server.application.coupon.service.CouponService;
import kr.hhplus.be.server.application.order.dto.OrderRequest;
import kr.hhplus.be.server.application.order.dto.OrderResponse;
import kr.hhplus.be.server.application.order.facade.OrderFacadeLockService;
import kr.hhplus.be.server.application.order.service.OrderProductService;
import kr.hhplus.be.server.application.product.service.ProductService;
import kr.hhplus.be.server.domain.order.OrderProduct;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // 테스트컨테이너에서 외부 DB 사용하도록 함
@ActiveProfiles("test") //application-test.yml 읽어오도록 함
@ContextConfiguration(classes = TestContainersConfiguration.class)//Spring boot Context 로딩 전 TestContainerConfiguration 읽어오게 하기 위함
public class OrderFacadeServiceTest {

    @Autowired
    OrderFacadeLockService orderFacadeLockService;

    @Autowired
    OrderProductService orderProductService;

    @Autowired
    ProductService productService;

    @Autowired
    CouponService couponService;

    @Test
//    @Transactional
//    @Commit
    @Sql(scripts = "/coupon.sql",executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD) //테스트 실행 시 해당 .sql 파일내의 쿼리 실행 -> 테이블 생성 후 실행됨
    @Sql(scripts = "/couponIssuedInfo.sql",executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD) //테스트 실행 시 해당 .sql 파일내의 쿼리 실행 -> 테이블 생성 후 실행됨
    @Sql(scripts = "/product.sql",executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD) //테스트 실행 시 해당 .sql 파일내의 쿼리 실행 -> 테이블 생성 후 실행됨
    @Sql(scripts = "/productOption.sql",executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD) //테스트 실행 시 해당 .sql 파일내의 쿼리 실행 -> 테이블 생성 후 실행됨
    @Sql(scripts = "/order.sql",executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD) //테스트 실행 시 해당 .sql 파일내의 쿼리 실행 -> 테이블 생성 후 실행됨
    @Sql(scripts = "/orderProduct.sql",executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD) //테스트 실행 시 해당 .sql 파일내의 쿼리 실행 -> 테이블 생성 후 실행됨
    @Sql(scripts = "/delete.sql",executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD) //이 클래스 테스트 종료 시 데이터 클랜징
    @DisplayName("주문 저장")
    void orderCreate() throws Exception {
        //Given
        OrderRequest.OrderCreate orderCreate = new OrderRequest.OrderCreate(1L, List.of(1L,1L,2L,3L,3L,3L),3L);

        // 0. product_option_id 오름차순으로 정렬
        List<Long> requestProductOptionIds = List.of(1L,1L,2L,3L,3L,3L);

        // 1. 옵션 ID 별로 등장 횟수 세기(세면서 정렬)
        Map<Long, Long> optionIdCountMap = requestProductOptionIds.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        //When
        OrderResponse.OrderDTO createResponse = orderFacadeLockService.createOrderWithLock(orderCreate,optionIdCountMap);

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

        //상품 재고 확인
        assertEquals(3L,createResponse.orderProduct().size());
        assertEquals(8L,productService.selectProductOptionByProductIdAndProductOptionId(1L,createResponse.orderProduct().get(0).productOptionId()).getStockQuantity());
        assertEquals(19L,productService.selectProductOptionByProductIdAndProductOptionId(1L,createResponse.orderProduct().get(1).productOptionId()).getStockQuantity());
        assertEquals(27L,productService.selectProductOptionByProductIdAndProductOptionId(1L,createResponse.orderProduct().get(2).productOptionId()).getStockQuantity());
    }

    @Test
//    @Transactional
//    @Commit
    @Sql(scripts = "/coupon.sql",executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD) //테스트 실행 시 해당 .sql 파일내의 쿼리 실행 -> 테이블 생성 후 실행됨
    @Sql(scripts = "/couponIssuedInfo.sql",executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD) //테스트 실행 시 해당 .sql 파일내의 쿼리 실행 -> 테이블 생성 후 실행됨
    @Sql(scripts = "/product.sql",executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD) //테스트 실행 시 해당 .sql 파일내의 쿼리 실행 -> 테이블 생성 후 실행됨
    @Sql(scripts = "/productOption.sql",executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD) //테스트 실행 시 해당 .sql 파일내의 쿼리 실행 -> 테이블 생성 후 실행됨
    @Sql(scripts = "/order.sql",executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD) //테스트 실행 시 해당 .sql 파일내의 쿼리 실행 -> 테이블 생성 후 실행됨
    @Sql(scripts = "/orderProduct.sql",executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD) //테스트 실행 시 해당 .sql 파일내의 쿼리 실행 -> 테이블 생성 후 실행됨
    @Sql(scripts = "/delete.sql",executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD) //이 클래스 테스트 종료 시 데이터 클랜징
    @DisplayName("주문 취소")
    void orderCancel() throws Exception {
        //Given
        OrderRequest.OrderCancel orderCancel = new OrderRequest.OrderCancel(1L, 16L);

        long requestOrderId = orderCancel.orderId();

        //주문 취소 대상 상품 조회
        List<OrderProduct> cancelOrderProduct = orderProductService.selectOrderProductsByOrderIdOrderByProductOptionIdAsc(requestOrderId);

        //상품 잔여 갯수 확인 및 복구
        // 0. 상품 ID, 주문 수량 셋팅
        Map<Long, Long> optionIdToQuantityMap = new HashMap<>();

        for (OrderProduct orderProduct : cancelOrderProduct) {
            Long optionId = orderProduct.getProductOptionId();
            Long quantity = orderProduct.getProductQuantity();

            optionIdToQuantityMap.merge(optionId, quantity, Long::sum);
        }

        //When
        OrderResponse.OrderDTO createResponse = orderFacadeLockService.cancelOrderWithLock(orderCancel,optionIdToQuantityMap);

        //Then
        //사용 쿠폰 확인
        assertEquals("테스트 쿠폰",createResponse.couponName());
        assertEquals(1_000L,createResponse.couponDiscountPrice());
        assertEquals("N",couponService.selectCouponByCouponIdAndUserId(createResponse.couponId(),1L).getUseYn());

        //주문 취소 확인
        assertEquals(16L,createResponse.orderId());
        assertEquals("cancel_order",createResponse.orderStatus());
        assertEquals(70_000L,createResponse.totalPrice());

        //상품 재고 확인
        assertEquals(3L,createResponse.orderProduct().size());
        assertEquals(12L,productService.selectProductOptionByProductIdAndProductOptionId(1L,createResponse.orderProduct().get(0).productOptionId()).getStockQuantity());
        assertEquals(21L,productService.selectProductOptionByProductIdAndProductOptionId(1L,createResponse.orderProduct().get(1).productOptionId()).getStockQuantity());
        assertEquals(33L,productService.selectProductOptionByProductIdAndProductOptionId(2L,createResponse.orderProduct().get(2).productOptionId()).getStockQuantity());
    }

}
