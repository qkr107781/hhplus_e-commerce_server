package kr.hhplus.be.server.application.order.facade;

import kr.hhplus.be.server.application.coupon.service.CouponService;
import kr.hhplus.be.server.application.order.dto.OrderBuilder;
import kr.hhplus.be.server.application.order.dto.OrderRequest;
import kr.hhplus.be.server.application.order.dto.OrderResponse;
import kr.hhplus.be.server.application.order.service.OrderProductService;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class OrderFacadeService implements OrderUseCase {

    private final OrderService orderService;
    private final OrderProductService orderProductService;
    private final ProductService productService;
    private final CouponService couponService;

    public OrderFacadeService(OrderService orderService, OrderProductService orderProductService, ProductService productService, CouponService couponService) {
        this.orderService = orderService;
        this.orderProductService = orderProductService;
        this.productService = productService;
        this.couponService = couponService;
    }

    @Transactional
    @Override
    public OrderResponse.OrderDTO createOrder(OrderRequest.OrderCreate orderRequest) throws Exception {
        long useCouponId = 0L;
        long couponDiscountPrice = 0L;
        long requestUserId = orderRequest.userId();
        long requestCouponId = orderRequest.couponId();
        long totalOrderPrice = 0L;

        //상품 잔여 갯수 확인 및 차감
        // 0. product_option_id 오름차순으로 정렬
        List<Long> requestProductOptionIds = orderRequest.productOptionIds().stream().sorted().toList();

        // 1. 옵션 ID 별로 등장 횟수 세기(세면서 정렬)
        Map<Long, Long> optionIdCountMap = requestProductOptionIds.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        // 2. 중복 제거한 ID로 product_option 목록 조회
        List<Long> uniqueOptionIds = new ArrayList<>(optionIdCountMap.keySet());
        List<ProductOption> productOptionList = productService.selectProductOptionByProductOptionIdInWithLock(uniqueOptionIds);

        // 3. 각 옵션의 등장 횟수만큼 재고 차감 및 결제 금액 산정을 위한 리스트 추가

        for (ProductOption productOption : productOptionList) {
            long optionId = productOption.getProductOptionId();
            long quantityToDecrease = optionIdCountMap.get(optionId);
            productService.decreaseStock(productOption, quantityToDecrease);
            totalOrderPrice += productService.calculateProductTotalPrice(productOption, quantityToDecrease);
        }

        //쿠폰 유효성 검증 및 사용
        CouponIssuedInfo couponIssuedInfo = couponService.useCoupon(requestCouponId,requestUserId,totalOrderPrice);
        Coupon coupon = couponService.selectCouponByCouponId(couponIssuedInfo.getCouponId());
        if("Y".equals(couponIssuedInfo.getUseYn())){
            useCouponId = couponIssuedInfo.getCouponId();
            couponDiscountPrice = coupon.getDiscountPrice();
        }

        //주문 생성
        OrderBuilder.Order createOrder = new OrderBuilder.Order(
                requestUserId,
                useCouponId,
                couponDiscountPrice,
                totalOrderPrice,
                "pending_payment",
                LocalDateTime.now()
        );
        Order afterCreateOrder = orderService.createOrder(OrderBuilder.Order.toDomain(createOrder));


        List<OrderResponse.OrderProductDTO> orderCreateProductResponseList = new ArrayList<>();
        Set<OrderBuilder.OrderProduct> seenOrderList = new HashSet<>();
        for(ProductOption productOption : productOptionList){
            long optionId = productOption.getProductOptionId();
            long orderQuantity = optionIdCountMap.get(optionId);

            //주문 상품 도메인 생성
            OrderBuilder.OrderProduct createOrderProduct = new OrderBuilder.OrderProduct(
                    afterCreateOrder.getOrderId(),
                    productOption.getProductId(),
                    productOption.getProductOptionId(),
                    orderQuantity,
                    productOption.getPrice()
            );

            if(seenOrderList.add(createOrderProduct)){
                OrderProduct afterCreatrOrderProduct = orderProductService.createOrderProduct(OrderBuilder.OrderProduct.toDomain(createOrderProduct));

                //주문 완료 상품 조회
                Product orderProduct = productService.selectProductByProductId(productOption.getProductId());
                //주문 상품 리턴 DTO 생성을 위함
                orderCreateProductResponseList.add(OrderResponse.OrderProductDTO.from(afterCreatrOrderProduct,orderProduct,productOption));
            }

        }

        //사용 쿠폰 정보 조회
        Coupon useCoupon = couponService.selectCouponByCouponId(requestCouponId);

        return OrderResponse.OrderDTO.from(afterCreateOrder,useCoupon,orderCreateProductResponseList);
    }

    @Transactional
    @Override
    public OrderResponse.OrderDTO cancelOrder(OrderRequest.OrderCancel orderRequest) throws Exception {
        long requestUserId = orderRequest.userId();
        long requestOrderId = orderRequest.orderId();

        //주문 취소 대상 주문 및 상품 조회
        Order cancelOrder = orderService.selectOrderByOrderId(requestOrderId);
        List<OrderProduct> cancelOrderProduct = orderProductService.selectOrderProductsByOrderIdOrderByProductOptionIdAsc(requestOrderId);

        //상품 잔여 갯수 확인 및 복구
        // 0. 상품 ID, 주문 수량 셋팅
        Map<Long, Long> optionIdToQuantityMap = new HashMap<>();

        for (OrderProduct orderProduct : cancelOrderProduct) {
            Long optionId = orderProduct.getProductOptionId();
            Long quantity = orderProduct.getProductQuantity();

            optionIdToQuantityMap.merge(optionId, quantity, Long::sum);
        }

        // 1. product_option_id 오름차순으로 정렬
        List<Long> uniqueOptionIds = new ArrayList<>(optionIdToQuantityMap.keySet()).stream().sorted().toList();

        // 2. 중복 제거한 ID로 product_option 목록 조회
        List<ProductOption> productOptionList = productService.selectProductOptionByProductOptionIdInWithLock(uniqueOptionIds);

        // 3. 각 옵션의 등장 횟수만큼 재고 차감
        List<ProductOption> afterRestoreProductOption = new ArrayList<>();
        for (ProductOption productOption : productOptionList) {
            long optionId = productOption.getProductOptionId();
            long quantityToRestore = optionIdToQuantityMap.get(optionId);

            afterRestoreProductOption.add(productService.restoreStock(productOption,quantityToRestore));
        }

        //쿠폰 사용 여부 확인 및 있다면 복구
        CouponIssuedInfo afterRestoreCouponIssuedInfo = couponService.restoreCoupon(requestUserId,cancelOrder.getCouponId());
        Coupon coupon = couponService.selectCouponByCouponId(afterRestoreCouponIssuedInfo.getCouponId());

        //주문 취소
        cancelOrder = orderService.cancelOrder(requestOrderId);

        //리턴 DTO 생성
        List<OrderResponse.OrderProductDTO> orderCancelProductResponseList = new ArrayList<>();
        for(ProductOption productOption : afterRestoreProductOption){

            //주문 취소 상품 조회
            Product product = productService.selectProductByProductId(productOption.getProductId());
            OrderProduct orderProduct = orderProductService.selectOrderProductByOrderIdAndProductOptionId(requestOrderId,productOption.getProductOptionId());

            orderCancelProductResponseList.add(OrderResponse.OrderProductDTO.from(orderProduct,product,productOption));
        }

        return OrderResponse.OrderDTO.from(cancelOrder,coupon,orderCancelProductResponseList);
    }
}
