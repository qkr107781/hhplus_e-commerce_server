package kr.hhplus.be.server.application.order.facade;

import kr.hhplus.be.server.application.coupon.service.CouponService;
import kr.hhplus.be.server.application.order.dto.OrderRequest;
import kr.hhplus.be.server.application.order.dto.OrderResponse;
import kr.hhplus.be.server.application.order.service.OrderService;
import kr.hhplus.be.server.application.order.service.OrderUseCase;
import kr.hhplus.be.server.application.product.service.ProductService;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponIssuedInfo;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderProduct;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductOption;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderFacadeService implements OrderUseCase {

    private final OrderService orderService;
    private final ProductService productService;
    private final CouponService couponService;

    public OrderFacadeService(OrderService orderService, ProductService productService, CouponService couponService) {
        this.orderService = orderService;
        this.productService = productService;
        this.couponService = couponService;
    }

    @Override
    public OrderResponse.OrderCreate createOrder(OrderRequest.OrderCreate orderRequest) throws Exception {
        long useCouponId = 0L;
        long couponDiscountPrice = 0L;
        long requestUserId = orderRequest.userId();
        long requestCouponId = orderRequest.couponId();
        List<Long> requestProductOptionIds = orderRequest.productOptionIds();

        //상품 잔여 갯수 확인 및 차감
        List<ProductOption> productOptionList = productService.decreaseStock(requestProductOptionIds);
        long totalOrderPrice = productService.calculateProductTotalPrice(productOptionList);
        if(totalOrderPrice == 0L){
            throw new Exception("empty total order price");
        }

        //쿠폰 유효성 검증 및 사용
        CouponIssuedInfo couponIssuedInfo = couponService.useCoupon(requestCouponId,requestUserId,totalOrderPrice);
        if("Y".equals(couponIssuedInfo.getUseYn())){
            useCouponId = couponIssuedInfo.getCoupon().getCouponId();
            couponDiscountPrice = couponIssuedInfo.getCoupon().getDiscountPrice();
        }

        //주문 도메인 생성
        Order createOrder = Order.builder()
                                .orderId(0L)
                                .userId(requestUserId)
                                .couponId(useCouponId)
                                .couponDiscountPrice(couponDiscountPrice)
                                .totalPrice(totalOrderPrice)
                                .orderStatus("pending_payment")
                                .orderDate(LocalDateTime.now())
                                .build();

        Order afterCreateOrder = orderService.createOrder(createOrder);


        List<OrderResponse.OrderCreateProduct> orderCreateProductResponseList = new ArrayList<>();
        for(ProductOption productOption : productOptionList){
            long orderQuantity = productOptionList.stream()
                    .filter(productOptionId -> productOptionId.getProductOptionId().equals(productOption.getProductOptionId()))
                    .count();

            //주문 상품 도메인 생성
            OrderProduct createOrderProduct = new OrderProduct(0L,afterCreateOrder.getOrderId(),productOption.getProductId(),productOption.getProductOptionId(),orderQuantity,productOption.getPrice());
            OrderProduct afterCreatrOrderProduct = orderService.createOrderProduct(createOrderProduct);

            //주문 완료 상품 조회
            Product orderProduct = productService.selectProductByProductId(productOption.getProductId());
            //주문 상품 리턴 DTO 생성을 위함
            orderCreateProductResponseList.add(OrderResponse.OrderCreateProduct.from(afterCreatrOrderProduct,orderProduct,productOption));
        }

        //사용 쿠폰 정보 조회
        Coupon useCoupon = couponService.selectCouponByCouponId(requestCouponId);

        return OrderResponse.OrderCreate.from(afterCreateOrder,useCoupon,orderCreateProductResponseList);
    }
}
