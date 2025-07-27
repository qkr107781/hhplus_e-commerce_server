package kr.hhplus.be.server.application.order.facade;

import kr.hhplus.be.server.application.coupon.service.CouponService;
import kr.hhplus.be.server.application.order.dto.OrderBuilder;
import kr.hhplus.be.server.application.order.dto.OrderRequest;
import kr.hhplus.be.server.application.order.dto.OrderResponse;
import kr.hhplus.be.server.application.order.service.OrderService;
import kr.hhplus.be.server.application.product.service.ProductService;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponIssuedInfo;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderProduct;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductOption;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
class OrderFacadeServiceTest {

    @Mock
    ProductService productService;

    @Mock
    CouponService couponService;

    @Mock
    OrderService orderService;

    @Test
    @DisplayName("[주문]Facade Service 주문 로직 테스트")
    void createOrder() throws Exception {
        //Given
        // 입력 파라미터 세팅
        long requestCouponId = 1L;
        long requestUserId = 1L;
        List<Long> requestProductOptionIds = List.of(1L, 2L, 3L);
        OrderRequest.OrderCreate orderRequest = new OrderRequest.OrderCreate(requestUserId, requestProductOptionIds, requestCouponId);

        long totalOrderPrice = 60_000L;
        long couponDiscountPrice = 1_000L;
        String orderStatus = "pending_payment";

        Product product = Product.builder()
                .productId(1L)
                .name("티셔츠")
                .description("티셔츠 설명")
                .build();

        // 상품 옵션 세팅 (재고 차감 전의 초기 상태)
        ProductOption productOption1 = new ProductOption(1L, product, "XL", 20_000L, 30L, 15L, "Y", LocalDateTime.now());
        ProductOption productOption2 = new ProductOption(2L, product, "L", 20_000L, 20L, 5L, "Y", LocalDateTime.now());
        ProductOption productOption3 = new ProductOption(3L, product, "M", 20_000L, 10L, 2L, "Y", LocalDateTime.now());
        List<ProductOption> products = new ArrayList<>();
        products.add(productOption1);
        products.add(productOption2);
        products.add(productOption3);

        // 1. 재고 차감 Mocking 수정: thenAnswer로 실제 재고를 차감하는 동작 흉내
        when(productService.decreaseStock(requestProductOptionIds)).thenAnswer(invocation -> {
            products.forEach(option -> {
                try {
                    option.decreaseProductQuantity();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }); // 각 옵션의 재고를 1씩 차감하는 로직
            return products;
        });

        // 2. 총 주문 금액 계산
        when(productService.calculateProductTotalPrice(products)).thenReturn(totalOrderPrice);

        // 쿠폰 세팅 (사용 전 상태)
        long couponId = 1L;
        long discountPrice = 1_000L;
        Coupon coupon = Coupon.builder()
                                .couponId(couponId)
                                .couponName("신규 가입 쿠폰")
                                .discountPrice(discountPrice)
                                .totalCouponAmount(30L)
                                .remainingCouponAmount(10L)
                                .minUsePrice(10_000L)
                                .issuanceStartTime(LocalDateTime.now().minusHours(1))
                                .issuanceEndTime(LocalDateTime.now().plusHours(1))
                                .useLimitTime(24L)
                                .couponStatus("issuing")
                                .regDate(LocalDateTime.now())
                                .build();
        CouponIssuedInfo couponIssuedInfo = CouponIssuedInfo.builder()
                                                            .couponIssuedId(1L)
                                                            .userId(1L)
                                                            .useYn("N")
                                                            .issuedAt(LocalDateTime.now())
                                                            .endDate(LocalDateTime.now().plusHours(24))
                                                            .coupon(coupon)
                                                            .build();

        // 3. 쿠폰 사용 Mocking 수정: thenAnswer로 useYn을 "Y"로 변경하는 동작 흉내
        when(couponService.useCoupon(anyLong(), anyLong(), anyLong())).thenAnswer(invocation -> {
            couponIssuedInfo.useCoupon(); // 쿠폰 사용 상태를 Y로 변경
            return couponIssuedInfo; // 변경된 객체를 반환
        });

        // 주문 및 주문 상품 Mocking
        Order order = Order.builder()
                .orderId(0L)
                .userId(requestUserId)
                .couponId(requestCouponId)
                .couponDiscountPrice(couponDiscountPrice)
                .totalPrice(totalOrderPrice)
                .orderStatus(orderStatus)
                .orderDate(LocalDateTime.now())
                .build();
        when(orderService.createOrder(any(Order.class))).thenReturn(order);
        when(orderService.createOrderProduct(any(OrderProduct.class))).thenAnswer(invocation -> invocation.getArgument(0, OrderProduct.class));

        //주문 완료 상품 Mocking
        Product afterOrderProduct = new Product(1L,"티셔츠", "티셔츠 설명",products);
        when(productService.selectProductByProductId(1L)).thenReturn(afterOrderProduct);

        //사용 쿠폰 Mocking
        when(couponService.selectCouponByCouponId(couponId)).thenReturn(coupon);

        //When
        OrderFacadeService orderFacadeService = new OrderFacadeService(orderService, productService, couponService);
        OrderResponse.OrderCreate result = orderFacadeService.createOrder(orderRequest);

        //Then
        // 재고 차감 검증 (thenAnswer가 실제 객체의 재고를 차감했으므로 검증 가능)
        assertEquals(14L, productOption1.getStockQuantity());
        assertEquals(4L, productOption2.getStockQuantity());
        assertEquals(1L, productOption3.getStockQuantity());

        // 쿠폰 사용 검증 (thenAnswer가 useYn을 Y로 바꿨으므로 검증 가능)
        assertEquals("Y", couponIssuedInfo.getUseYn());

        // 주문 검증
        assertEquals(totalOrderPrice, result.totalPrice());

        verify(productService, times(1)).decreaseStock(requestProductOptionIds);
        verify(couponService, times(1)).useCoupon(requestCouponId, requestUserId, totalOrderPrice);
        verify(orderService, times(1)).createOrder(any(Order.class));
        verify(orderService, times(3)).createOrderProduct(any(OrderProduct.class));
    }
}