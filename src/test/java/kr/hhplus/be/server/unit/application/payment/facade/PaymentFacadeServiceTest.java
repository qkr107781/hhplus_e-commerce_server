package kr.hhplus.be.server.unit.application.payment.facade;

import kr.hhplus.be.server.application.balance.service.BalanceService;
import kr.hhplus.be.server.application.coupon.service.CouponService;
import kr.hhplus.be.server.application.order.service.OrderProductService;
import kr.hhplus.be.server.application.order.service.OrderService;
import kr.hhplus.be.server.application.payment.dto.PaymentRequest;
import kr.hhplus.be.server.application.payment.dto.PaymentResponse;
import kr.hhplus.be.server.application.payment.facade.PaymentFacadeService;
import kr.hhplus.be.server.application.payment.service.PaymentService;
import kr.hhplus.be.server.application.product.service.ProductService;
import kr.hhplus.be.server.domain.balance.Balance;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponIssuedInfo;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderProduct;
import kr.hhplus.be.server.domain.payment.Payment;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.doAnswer;

@ExtendWith(MockitoExtension.class)
class PaymentFacadeServiceTest {

    @Mock
    ProductService productService;

    @Mock
    CouponService couponService;

    @Mock
    OrderService orderService;

    @Mock
    OrderProductService orderProductService;

    @Mock
    PaymentService paymentService;

    @Mock
    BalanceService balanceService;

    @Test
    @DisplayName("[결제] 결제 성공")
    void createPayment() throws Exception{
        //Given

        Order order = Order.builder()
                .orderId(1L)
                .userId(1L)
                .couponId(1L)
                .couponDiscountPrice(1_000L)
                .totalPrice(50_000L)
                .orderStatus("pending_payment")
                .orderDate(LocalDateTime.now())
                .build();

        OrderProduct orderProduct1 = OrderProduct.builder()
                .orderProductId(1L)
                .orderId(order.getOrderId())
                .productId(1L)
                .productOptionId(1L)
                .productQuantity(2L)
                .productPrice(10_000L)
                .build();
        OrderProduct orderProduct2 = OrderProduct.builder()
                .orderProductId(2L)
                .orderId(order.getOrderId())
                .productId(1L)
                .productOptionId(2L)
                .productQuantity(3L)
                .productPrice(10_000L)
                .build();
        List<OrderProduct> orderProductList = new ArrayList<>();
        orderProductList.add(orderProduct1);
        orderProductList.add(orderProduct2);

        when(orderService.selectOrderByOrderId(1L)).thenReturn(order);
        when(orderProductService.selectOrderProductsByOrderIdOrderByProductOptionIdAsc(1L)).thenReturn(orderProductList);

        Balance balance = Balance.builder()
                .balanceId(1L)
                .userId(1L)
                .balance(100_000L)
                .lastChargeDate(LocalDateTime.now())
                .build();

        when(balanceService.selectBalanceByUserIdUseInFacade(1L)).thenReturn(balance);

        doAnswer(invocation -> {
            Balance targetBalance = invocation.getArgument(0);
            targetBalance.useBalance(49_000L);
            return null;
        }).when(balanceService).useBalance(any(Balance.class), eq(49_000L));

        doAnswer(invocation -> {
            Order targetOrder = invocation.getArgument(0);
            targetOrder.updateOrderStatusToPayment();
            return null;
        }).when(orderService).updateOrderStatusToPayment(any(Order.class));

        Payment payment = Payment.builder()
                .paymentId(1L)
                .userId(1L)
                .orderId(1L)
                .paymentPrice(50_000L)
                .paymentDate(LocalDateTime.now())
                .build();

        when(paymentService.createPayment(any(Payment.class))).thenReturn(payment);

        Coupon coupon = Coupon.builder()
                .couponId(1L)
                .couponName("신규 가입 쿠폰")
                .discountPrice(1_000L)
                .totalCouponAmount(30L)
                .remainingCouponAmount(10L)
                .issuanceStartTime(LocalDateTime.now().minusHours(2))
                .issuanceEndTime(LocalDateTime.now().plusHours(1))
                .couponStatus("issuing")
                .regDate(LocalDateTime.now().minusHours(3))
                .build();

        CouponIssuedInfo couponIssuedInfo = CouponIssuedInfo.builder()
                .couponIssuedId(1L)
                .userId(1L)
                .useYn("Y")
                .issuedAt(LocalDateTime.now().minusHours(1))
                .endDate(LocalDateTime.now().plusHours(23))
                .couponId(coupon.getCouponId())
                .build();

        when(couponService.selectCouponByCouponId(1L)).thenReturn(coupon);
        when(couponService.selectCouponByCouponIdAndUserId(1L,1L)).thenReturn(couponIssuedInfo);

        Product product = Product.builder()
                .productId(1L)
                .name("티셔츠")
                .build();

        ProductOption productOption1 = ProductOption.builder()
                .productOptionId(1L)
                .productId(product.getProductId())
                .optionName("L")
                .price(10_000L)
                .salesYn("Y")
                .regDate(LocalDateTime.now().minusHours(1))
                .build();
        ProductOption productOption2 = ProductOption.builder()
                .productOptionId(2L)
                .productId(product.getProductId())
                .optionName("M")
                .price(10_000L)
                .salesYn("Y")
                .regDate(LocalDateTime.now().minusHours(1))
                .build();

        when(productService.selectProductByProductId(1L)).thenReturn(product);
        when(productService.selectProductOptionByProductIdAndProductOptionId(1L,1L)).thenReturn(productOption1);
        when(productService.selectProductOptionByProductIdAndProductOptionId(1L,2L)).thenReturn(productOption2);

        //When
        PaymentRequest.Create request = new PaymentRequest.Create(1L,1L);
        PaymentFacadeService paymentFacadeService = new PaymentFacadeService(balanceService,paymentService,orderService,orderProductService,couponService,productService);
        PaymentResponse.Create response = paymentFacadeService.createPayment(request);

// Then
// 1. PaymentFacadeService가 반환한 응답이 올바른지 검증
        assertNotNull(response);
        assertEquals(1L, response.paymentId());
        assertEquals(1L, response.order().orderId());
        assertEquals(50_000L, response.paymentPrice());

// 2. Mocking한 객체들의 상태가 변경되었는지 검증

// 2-1. 주문(Order) 상태 검증
// doAnswer를 통해 Order 객체의 상태가 "payment_completed"로 변경되었는지 확인
        assertEquals("payment_completed", order.getOrderStatus());
// order.totalPrice는 변경되지 않았으므로 그대로 유지되는지 확인
        assertEquals(50_000L, order.getTotalPrice());

// 2-2. 잔액(Balance) 상태 검증
// doAnswer를 통해 Balance 객체의 잔액이 49,000L 만큼 차감되었는지 확인
        assertEquals(100_000L - 49_000L, balance.getBalance());

// 2-3. 쿠폰(CouponIssuedInfo) 사용 여부 검증
// couponService.selectCouponByCouponIdAndUserId(1L,1L)의 반환 객체인
// couponIssuedInfo의 useYn이 "Y"로 변경되었는지 확인
        assertEquals("Y", couponIssuedInfo.getUseYn());

// 3. 의존하는 서비스 메서드들이 올바르게 호출되었는지 검증 (verify)

// 3-1. OrderService 메서드 호출 검증
        verify(orderService, times(1)).selectOrderByOrderId(1L);
        verify(orderService, times(1)).updateOrderStatusToPayment(order);

// 3-2. BalanceService 메서드 호출 검증
        verify(balanceService, times(1)).selectBalanceByUserIdUseInFacade(1L);
        verify(balanceService, times(1)).useBalance(balance, 49_000L);

// 3-3. PaymentService 메서드 호출 검증
// createPayment 메서드가 Payment 객체를 인자로 받아 호출되었는지 확인
        verify(paymentService, times(1)).createPayment(any(Payment.class));

// 3-4. CouponService 메서드 호출 검증
        verify(couponService, times(1)).selectCouponByCouponIdAndUserId(1L,1L);

// 3-5. ProductService 메서드 호출 검증
// 주문 상품 2개에 대한 재고 확인 및 차감이 이루어졌으므로 총 4번의 호출이 예상됨
        verify(productService, times(2)).selectProductByProductId(1L);
        verify(productService, times(1)).selectProductOptionByProductIdAndProductOptionId(1L, 1L);
        verify(productService, times(1)).selectProductOptionByProductIdAndProductOptionId(1L, 2L);
    }

}