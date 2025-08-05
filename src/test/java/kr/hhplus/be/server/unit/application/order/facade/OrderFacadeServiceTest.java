package kr.hhplus.be.server.unit.application.order.facade;

import kr.hhplus.be.server.application.coupon.service.CouponService;
import kr.hhplus.be.server.application.order.dto.OrderRequest;
import kr.hhplus.be.server.application.order.dto.OrderResponse;
import kr.hhplus.be.server.application.order.facade.OrderFacadeService;
import kr.hhplus.be.server.application.order.service.OrderProductService;
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

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

    @Mock
    OrderProductService orderProductService;

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
                .build();

        // 상품 옵션 세팅 (재고 차감 전의 초기 상태)
        ProductOption productOption1 = ProductOption.builder()
                .productOptionId(1L)
                .productId(product.getProductId())
                .optionName("XL")
                .price(20_000L)
                .salesYn("Y")
                .totalQuantity(30L)
                .stockQuantity(15L)
                .regDate(LocalDateTime.now())
                .build();
        ProductOption productOption2 = ProductOption.builder()
                .productOptionId(2L)
                .productId(product.getProductId())
                .optionName("L")
                .price(20_000L)
                .salesYn("Y")
                .totalQuantity(30L)
                .stockQuantity(5L)
                .regDate(LocalDateTime.now())
                .build();
        ProductOption productOption3 = ProductOption.builder()
                .productOptionId(3L)
                .productId(product.getProductId())
                .optionName("M")
                .price(20_000L)
                .salesYn("Y")
                .totalQuantity(30L)
                .stockQuantity(2L)
                .regDate(LocalDateTime.now())
                .build();

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
                                .issuanceStartTime(LocalDateTime.now().minusHours(1))
                                .issuanceEndTime(LocalDateTime.now().plusHours(1))
                                .couponStatus("issuing")
                                .regDate(LocalDateTime.now())
                                .build();
        CouponIssuedInfo couponIssuedInfo = CouponIssuedInfo.builder()
                                                            .couponIssuedId(1L)
                                                            .userId(1L)
                                                            .useYn("N")
                                                            .issuedAt(LocalDateTime.now())
                                                            .endDate(LocalDateTime.now().plusHours(24))
                                                            .couponId(coupon.getCouponId())
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
        // `AtomicLong`을 사용해 호출될 때마다 ID를 1씩 증가시킴
        AtomicLong orderProductIdCounter = new AtomicLong(1);

        when(orderProductService.createOrderProduct(any(OrderProduct.class))).thenAnswer(invocation -> {
            OrderProduct originalOrderProduct = invocation.getArgument(0, OrderProduct.class);
            // 새로운 ID를 할당하여 반환
            return OrderProduct.builder()
                    .orderProductId(orderProductIdCounter.getAndIncrement())
                    .orderId(originalOrderProduct.getOrderId())
                    .productId(originalOrderProduct.getProductId())
                    .productOptionId(originalOrderProduct.getProductOptionId())
                    .productQuantity(originalOrderProduct.getProductQuantity())
                    .productPrice(originalOrderProduct.getProductPrice())
                    .build();
        });

        //주문 완료 상품 Mocking
        Product afterOrderProduct = new Product(1L,"티셔츠");
        when(productService.selectProductByProductId(1L)).thenReturn(afterOrderProduct);

        //사용 쿠폰 Mocking
        when(couponService.selectCouponByCouponId(couponId)).thenReturn(coupon);

        //When
        OrderFacadeService orderFacadeService = new OrderFacadeService(orderService, orderProductService, productService, couponService);
        OrderResponse.OrderDTO result = orderFacadeService.createOrder(orderRequest);

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
        verify(orderProductService, times(3)).createOrderProduct(any(OrderProduct.class));
    }

    @Test
    @DisplayName("[주문 취소]Facade Service 주문 취소(재고 복구, 쿠폰 복구, 주문 취소)")
    void cancelOrder() throws Exception {
        //Given
        long cancelOrderId = 1L;
        long cancelCouponId = 1L;
        long[] cancelProductOptionIds = {1L,2L,3L};//3개, 4개, 5개

        Order order = Order.builder()
                .orderId(cancelOrderId)
                .userId(1L)
                .couponId(cancelCouponId)
                .orderStatus("pending_payment")
                .couponDiscountPrice(100L)
                .totalPrice(12_000L)
                .build();

        when(orderService.selectOrderByOrderId(cancelOrderId)).thenReturn(order);

        OrderProduct orderProduct_1 = OrderProduct.builder()
                .orderProductId(1L)
                .orderId(cancelOrderId)
                .productId(1L)
                .productOptionId(cancelProductOptionIds[0])
                .productQuantity(3L)
                .productPrice(1_000L)
                .build();

        OrderProduct orderProduct_2 = OrderProduct.builder()
                .orderProductId(2L)
                .orderId(cancelOrderId)
                .productId(1L)
                .productOptionId(cancelProductOptionIds[1])
                .productQuantity(4L)
                .productPrice(1_000L)
                .build();

        OrderProduct orderProduct_3 = OrderProduct.builder()
                .orderProductId(3L)
                .orderId(cancelOrderId)
                .productId(1L)
                .productOptionId(cancelProductOptionIds[2])
                .productQuantity(5L)
                .productPrice(1_000L)
                .build();

        List<OrderProduct> orderProductList = new ArrayList<>();
        orderProductList.add(orderProduct_1);
        orderProductList.add(orderProduct_2);
        orderProductList.add(orderProduct_3);

        when(orderProductService.selectOrderProductsByOrderId(cancelOrderId)).thenReturn(orderProductList);

        ProductOption productOption_1 = ProductOption.builder()
                .productOptionId(1L)
                .productId(1L)
                .optionName("M")
                .totalQuantity(30L)
                .stockQuantity(20L)
                .build();

        ProductOption productOption_2 = ProductOption.builder()
                .productOptionId(2L)
                .productId(1L)
                .optionName("L")
                .totalQuantity(30L)
                .stockQuantity(20L)
                .build();

        ProductOption productOption_3 = ProductOption.builder()
                .productOptionId(3L)
                .productId(1L)
                .optionName("XL")
                .totalQuantity(30L)
                .stockQuantity(20L)
                .build();

        List<ProductOption> productOptionList = new ArrayList<>();
        productOptionList.add(productOption_1);
        productOptionList.add(productOption_2);
        productOptionList.add(productOption_3);

        AtomicInteger i = new AtomicInteger();
        when(productService.restoreStock(orderProductList)).thenAnswer(invocation -> {
            productOptionList.forEach(option -> {
                try {
                    option.restoreProductQuantity(orderProductList.get(i.get()).getProductQuantity());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                i.getAndIncrement();
            });
            return productOptionList;
        });

        when(productService.restoreStock(orderProductList)).thenReturn(productOptionList);

        CouponIssuedInfo couponIssuedInfo = CouponIssuedInfo.builder()
                .userId(1L)
                .useYn("Y")
                .couponId(cancelCouponId)
                .build();

        when(couponService.restoreCoupon(anyLong(), anyLong())).thenAnswer(invocation -> {
            couponIssuedInfo.unuseCoupon();
            return couponIssuedInfo;
        });

        Coupon coupon = Coupon.builder()
                .couponId(cancelCouponId)
                .couponName("여름 쿠폰")
                .build();

        when(couponService.selectCouponByCouponId(cancelCouponId)).thenReturn(coupon);

        when(orderService.cancelOrder(cancelOrderId)).thenAnswer(invocation -> {
            order.cancelOrder();
            return order;
        });

        Product product = Product.builder()
                .productId(1L)
                .name("반팔")
                .build();

        when(productService.selectProductByProductId(any(Long.class))).thenReturn(product);

        when(orderProductService.selectOrderProductByOrderIdAndProductOptionId(cancelOrderId,1L)).thenReturn(orderProduct_1);
        when(orderProductService.selectOrderProductByOrderIdAndProductOptionId(cancelOrderId,2L)).thenReturn(orderProduct_2);
        when(orderProductService.selectOrderProductByOrderIdAndProductOptionId(cancelOrderId,3L)).thenReturn(orderProduct_3);
        //When
        OrderRequest.OrderCancel orderRequest = new OrderRequest.OrderCancel(1L,1L);

        OrderFacadeService orderFacadeService = new OrderFacadeService(orderService, orderProductService, productService, couponService);
        OrderResponse.OrderDTO result = orderFacadeService.cancelOrder(orderRequest);

        //Then
        // 재고 차감 검증 (thenAnswer가 실제 객체의 재고를 차감했으므로 검증 가능)
        assertEquals(23L, productOption_1.getStockQuantity());
        assertEquals(24L, productOption_2.getStockQuantity());
        assertEquals(25L, productOption_3.getStockQuantity());

        // 쿠폰 사용 검증 (thenAnswer가 useYn을 Y로 바꿨으므로 검증 가능)
        assertEquals("N", couponIssuedInfo.getUseYn());

        // 주문 검증
        assertEquals("cancel_order", result.orderStatus());

        verify(orderService, times(1)).selectOrderByOrderId(cancelOrderId);
        verify(orderProductService, times(1)).selectOrderProductsByOrderId(cancelOrderId);
        verify(productService, times(1)).restoreStock(orderProductList);
        verify(couponService, times(1)).restoreCoupon(1L,cancelCouponId);
        verify(couponService, times(1)).selectCouponByCouponId(cancelCouponId);
        verify(orderService, times(1)).cancelOrder(cancelOrderId);
        verify(productService, times(3)).selectProductByProductId(any(Long.class));
        verify(orderProductService, times(3)).selectOrderProductByOrderIdAndProductOptionId(any(Long.class),any(Long.class));
    }
}