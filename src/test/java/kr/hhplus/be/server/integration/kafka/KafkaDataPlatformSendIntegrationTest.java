package kr.hhplus.be.server.integration.kafka;

import kr.hhplus.be.server.TestContainersConfiguration;
import kr.hhplus.be.server.application.balance.service.BalanceService;
import kr.hhplus.be.server.application.coupon.service.CouponService;
import kr.hhplus.be.server.application.kafka.repository.producer.KafkaProducerRepository;
import kr.hhplus.be.server.application.order.service.OrderProductService;
import kr.hhplus.be.server.application.order.service.OrderService;
import kr.hhplus.be.server.application.payment.dto.PaymentRequest;
import kr.hhplus.be.server.application.payment.dto.PaymentResponse;
import kr.hhplus.be.server.application.payment.facade.PaymentFacadeService;
import kr.hhplus.be.server.application.payment.service.PaymentService;
import kr.hhplus.be.server.application.product.service.ProductService;
import kr.hhplus.be.server.common.kafka.KafkaConstants;
import kr.hhplus.be.server.domain.balance.Balance;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponIssuedInfo;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderProduct;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductOption;
import kr.hhplus.be.server.persistence.external.kafka.consumer.KafkaConsumer;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class KafkaDataPlatformSendIntegrationTest extends TestContainersConfiguration {

    @Mock ProductService productService;
    @Mock CouponService couponService;
    @Mock OrderService orderService;
    @Mock OrderProductService orderProductService;
    @Mock PaymentService paymentService;
    @Mock BalanceService balanceService;
    @Mock ApplicationEventPublisher publisher;
    @Autowired
    KafkaProducerRepository<PaymentResponse.Create> kafkaProducer;
    @MockitoSpyBean
    KafkaConsumer kafkaConsumer;

    @Test
    void paymentCompleteKafkaPubSubTest() throws Exception {
        //Given
        Order order = Order.builder()
                .orderId(1L).userId(1L).couponId(1L)
                .couponDiscountPrice(1_000L).totalPrice(50_000L)
                .orderStatus("pending_payment").orderDate(LocalDateTime.now())
                .build();

        OrderProduct orderProduct1 = OrderProduct.builder()
                .orderProductId(1L).orderId(order.getOrderId())
                .productId(1L).productOptionId(1L)
                .productQuantity(2L).productPrice(10_000L).build();
        OrderProduct orderProduct2 = OrderProduct.builder()
                .orderProductId(2L).orderId(order.getOrderId())
                .productId(1L).productOptionId(2L)
                .productQuantity(3L).productPrice(10_000L).build();
        List<OrderProduct> orderProductList = new ArrayList<>();
        orderProductList.add(orderProduct1);
        orderProductList.add(orderProduct2);

        when(orderService.selectOrderByOrderIdWithLock(1L)).thenReturn(order);
        when(orderProductService.selectOrderProductsByOrderIdOrderByProductOptionIdAsc(1L))
                .thenReturn(orderProductList);

        Balance balance = Balance.builder()
                .balanceId(1L).userId(1L).balance(100_000L)
                .lastChargeDate(LocalDateTime.now()).build();

        doAnswer(invocation -> {
            balance.useBalance(49_000L);
            return null;
        }).when(balanceService).useBalance(1L, 49_000L);

        doAnswer(invocation -> {
            Order targetOrder = invocation.getArgument(0);
            targetOrder.updateOrderStatusToPayment();
            return null;
        }).when(orderService).updateOrderStatusToPayment(any(Order.class));

        Payment payment = Payment.builder()
                .paymentId(1L).userId(1L).orderId(1L)
                .paymentPrice(50_000L).paymentDate(LocalDateTime.now()).build();

        when(paymentService.createPayment(any(Payment.class))).thenReturn(payment);

        Coupon coupon = Coupon.builder()
                .couponId(1L).couponName("신규 가입 쿠폰")
                .discountPrice(1_000L).totalCouponAmount(30L)
                .remainingCouponAmount(10L)
                .issuanceStartTime(LocalDateTime.now().minusHours(2))
                .issuanceEndTime(LocalDateTime.now().plusHours(1))
                .couponStatus("issuing").regDate(LocalDateTime.now().minusHours(3))
                .build();

        CouponIssuedInfo couponIssuedInfo = CouponIssuedInfo.builder()
                .couponIssuedId(1L).userId(1L).useYn("Y")
                .issuedAt(LocalDateTime.now().minusHours(1))
                .endDate(LocalDateTime.now().plusHours(23))
                .couponId(coupon.getCouponId()).build();

        when(couponService.selectCouponByCouponId(1L)).thenReturn(coupon);
        when(couponService.selectCouponByCouponIdAndUserId(1L, 1L))
                .thenReturn(couponIssuedInfo);

        Product product = Product.builder().productId(1L).name("티셔츠").build();

        ProductOption productOption1 = ProductOption.builder()
                .productOptionId(1L).productId(product.getProductId())
                .optionName("L").price(10_000L).salesYn("Y")
                .regDate(LocalDateTime.now().minusHours(1)).build();
        ProductOption productOption2 = ProductOption.builder()
                .productOptionId(2L).productId(product.getProductId())
                .optionName("M").price(10_000L).salesYn("Y")
                .regDate(LocalDateTime.now().minusHours(1)).build();

        when(productService.selectProductByProductId(1L)).thenReturn(product);
        when(productService.selectProductOptionByProductIdAndProductOptionId(1L, 1L))
                .thenReturn(productOption1);
        when(productService.selectProductOptionByProductIdAndProductOptionId(1L, 2L))
                .thenReturn(productOption2);

        //When
        PaymentRequest.Create request = new PaymentRequest.Create(1L, 1L);
        PaymentFacadeService paymentFacadeService = new PaymentFacadeService(balanceService, paymentService,orderService, orderProductService,couponService, productService, publisher);
        PaymentResponse.Create response = paymentFacadeService.createPayment(request);

        //Then
        // Kafka 이벤트 발행 (Config 기반 KafkaTemplate 사용)
        kafkaProducer.send(KafkaConstants.PAYMENT_COMPLETE_TOPIC,response);

        // Kafka 메시지가 컨슈머에 의해 처리될 시간을 기다림 (최대 3초)
        verify(kafkaConsumer, timeout(3000)).consumePaymentComplete(any(String.class));
        verify(kafkaConsumer, times(1)).consumePaymentComplete(any(String.class));
    }
}