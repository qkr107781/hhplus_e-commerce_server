package kr.hhplus.be.server.application.payment.facade;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import kr.hhplus.be.server.application.balance.service.BalanceService;
import kr.hhplus.be.server.application.coupon.service.CouponService;
import kr.hhplus.be.server.application.order.dto.OrderResponse;
import kr.hhplus.be.server.application.order.service.OrderProductService;
import kr.hhplus.be.server.application.order.service.OrderService;
import kr.hhplus.be.server.application.payment.dto.PaymentBuilder;
import kr.hhplus.be.server.application.payment.dto.PaymentRequest;
import kr.hhplus.be.server.application.payment.dto.PaymentResponse;
import kr.hhplus.be.server.application.payment.service.PaymentService;
import kr.hhplus.be.server.application.payment.service.PaymentUseCase;
import kr.hhplus.be.server.application.product.service.ProductService;
import kr.hhplus.be.server.domain.balance.Balance;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponIssuedInfo;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderProduct;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductOption;
import kr.hhplus.be.server.persistence.dataplatform.AsyncDataPlatformSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class PaymentFacadeService implements PaymentUseCase {

    private final BalanceService balanceService;
    private final PaymentService paymentService;
    private final OrderService orderService;
    private final OrderProductService orderProductService;
    private final CouponService couponService;
    private final ProductService productService;

    public PaymentFacadeService(BalanceService balanceService, PaymentService paymentService, OrderService orderService, OrderProductService orderProductService, CouponService couponService, ProductService productService) {
        this.balanceService = balanceService;
        this.paymentService = paymentService;
        this.orderService = orderService;
        this.orderProductService = orderProductService;
        this.couponService = couponService;
        this.productService = productService;
    }

    /**
     * 결제
     * @param request: 사용자 ID, 주문 ID
     * @return PaymentResponse.Create
     * @throws Exception
     */
    @Transactional
    @Override
    public PaymentResponse.Create createPayment(PaymentRequest.Create request) throws Exception {
        long orderId = request.orderId();
        long userId = request.userId();
        long paymentPrice = 0L;

        //주문 조회
        Order order = orderService.selectOrderByOrderId(orderId);
        if(order == null){
            throw new Exception("order empty");
        }
        paymentPrice = order.getTotalPrice() - order.getCouponDiscountPrice();

        //잔액 조회
        Balance balance = balanceService.selectBalanceByUserIdUseInFacade(userId);
        if(balance.getBalance() == 0 || balance.getBalance() < paymentPrice){
            throw new Exception("not enough balance");
        }

        //잔액 차감
        balanceService.useBalance(balance, paymentPrice);

        //주문 상태 변경
        orderService.updateOrderStatusToPayment(order);

        //결제 insert
        PaymentBuilder.Payment payment = new PaymentBuilder.Payment(
                0L,
                userId,
                orderId,
                paymentPrice,
                LocalDateTime.now()
        );
        Payment afterCreatePayment = paymentService.createPayment(PaymentBuilder.Payment.toDomain(payment));


        //Response 객체 생성
        //사용 쿠폰 조회
        CouponIssuedInfo couponIssuedInfo = couponService.selectCouponByCouponIdAndUserId(order.getCouponId(), userId);
        Coupon coupon = couponService.selectCouponByCouponId(couponIssuedInfo.getCouponId());

        //주문 상품 목록 조회
        List<OrderProduct> productOptionList = orderProductService.selectOrderProductsByOrderId(order.getOrderId());
        List<OrderResponse.OrderCreateProduct> orderCreateProductList = new ArrayList<>();
        for(OrderProduct orderProduct : productOptionList){
            long productId = orderProduct.getProductId();
            long productOptionId = orderProduct.getProductOptionId();

            Product product = productService.selectProductByProductId(productId);
            ProductOption productOption = productService.selectProductOptionByProductIdAndProductOptionId(productId,productOptionId);

            orderCreateProductList.add(OrderResponse.OrderCreateProduct.from(orderProduct,product,productOption));
        }
        //Response 객체 생성 완료
        PaymentResponse.Create response = PaymentResponse.Create.from(afterCreatePayment,order,coupon,orderCreateProductList);

        //결제 내역 데이터 플랫폼 API 전송(비동기)
        AsyncDataPlatformSender sender = new AsyncDataPlatformSender("http://testestest.com");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String jsonData = objectMapper.writeValueAsString(response);

        //데이터 전송
        CompletableFuture<Boolean> future1 = sender.sendDataAsync(jsonData);
        future1.thenAccept(success -> {
            if (success) {
                System.out.println("Result 1: Data sent successfully!");
            } else {
                System.err.println("Result 1: Failed to send data.");
            }
        });

        return response;
    }
}
