package kr.hhplus.be.server.application.payment.facade;

import kr.hhplus.be.server.application.balance.service.BalanceService;
import kr.hhplus.be.server.application.coupon.service.CouponService;
import kr.hhplus.be.server.application.order.dto.OrderResponse;
import kr.hhplus.be.server.application.order.service.OrderProductService;
import kr.hhplus.be.server.application.order.service.OrderService;
import kr.hhplus.be.server.application.payment.dto.PaymentBuilder;
import kr.hhplus.be.server.application.payment.dto.PaymentRequest;
import kr.hhplus.be.server.application.payment.dto.PaymentResponse;
import kr.hhplus.be.server.application.payment.event.PaymentCreateEvent;
import kr.hhplus.be.server.application.payment.service.PaymentService;
import kr.hhplus.be.server.application.payment.service.PaymentUseCase;
import kr.hhplus.be.server.application.product.dto.ProductResponse;
import kr.hhplus.be.server.application.product.service.ProductService;
import kr.hhplus.be.server.common.redis.DistributedLock;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponIssuedInfo;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderProduct;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductOption;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentFacadeService implements PaymentUseCase {

    private final BalanceService balanceService;
    private final PaymentService paymentService;
    private final OrderService orderService;
    private final OrderProductService orderProductService;
    private final CouponService couponService;
    private final ProductService productService;
    private final ApplicationEventPublisher publisher;

    public PaymentFacadeService(BalanceService balanceService, PaymentService paymentService, OrderService orderService, OrderProductService orderProductService, CouponService couponService, ProductService productService, ApplicationEventPublisher publisher) {
        this.balanceService = balanceService;
        this.paymentService = paymentService;
        this.orderService = orderService;
        this.orderProductService = orderProductService;
        this.couponService = couponService;
        this.productService = productService;
        this.publisher = publisher;
    }

    /**
     * 결제
     * @param request: 사용자 ID, 주문 ID
     * @return PaymentResponse.Create
     * @throws Exception
     */
    @DistributedLock(
            keys = {
                    "'user:balance:' + #request.userId"
            }
    )
    @Transactional
    @Override
    public PaymentResponse.Create createPayment(PaymentRequest.Create request) throws Exception {
        long orderId = request.orderId();
        long userId = request.userId();
        long paymentPrice = 0L;

        //주문 조회
        Order order = orderService.selectOrderByOrderIdWithLock(orderId);
        if(order == null){
            throw new Exception("order empty");
        }
        paymentPrice = order.getTotalPrice() - order.getCouponDiscountPrice();

        //주문 상태 변경
        orderService.updateOrderStatusToPayment(order);

        //잔액 차감
        balanceService.useBalance(userId, paymentPrice);

        //결제 insert
        PaymentBuilder.Payment payment = new PaymentBuilder.Payment(
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
        List<OrderProduct> productOptionList = orderProductService.selectOrderProductsByOrderIdOrderByProductOptionIdAsc(order.getOrderId());
        List<OrderResponse.OrderProductDTO> orderCreateProductList = new ArrayList<>();

        //레디스 인기상품 데이터 Sorted Sets 입력을 위한 전송 데이터
        List<ProductResponse.StatisticsRedis> redisSendDataList = new ArrayList<>();

        for(OrderProduct orderProduct : productOptionList){
            long productId = orderProduct.getProductId();
            long productOptionId = orderProduct.getProductOptionId();

            Product product = productService.selectProductByProductId(productId);
            ProductOption productOption = productService.selectProductOptionByProductIdAndProductOptionId(productId,productOptionId);

            orderCreateProductList.add(OrderResponse.OrderProductDTO.from(orderProduct,product,productOption));

            redisSendDataList.add(new ProductResponse.StatisticsRedis(productOptionId,orderProduct.getProductQuantity()));
        }
        //Response 객체 생성 완료
        PaymentResponse.Create response = PaymentResponse.Create.from(afterCreatePayment,order,coupon,orderCreateProductList);

        //데이터 플랫폼 API 비동기 호출
        publisher.publishEvent(new PaymentCreateEvent.SendDataPlatform(response));
        //레디스 인기상품 데이터 Sorted Sets 입력을 위한 전송
        publisher.publishEvent(new PaymentCreateEvent.SendRedis(redisSendDataList));

        return response;
    }
}
