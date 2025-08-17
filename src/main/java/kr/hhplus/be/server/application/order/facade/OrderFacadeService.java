package kr.hhplus.be.server.application.order.facade;

import kr.hhplus.be.server.application.order.dto.OrderRequest;
import kr.hhplus.be.server.application.order.dto.OrderResponse;
import kr.hhplus.be.server.application.order.service.OrderProductService;
import kr.hhplus.be.server.application.order.service.OrderUseCase;
import kr.hhplus.be.server.domain.order.OrderProduct;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class OrderFacadeService implements OrderUseCase {

    private final OrderProductService orderProductService;
    private final OrderFacadeLockService orderFacadeLockService;

    public OrderFacadeService(OrderProductService orderProductService, OrderFacadeLockService orderFacadeLockService) {
        this.orderProductService = orderProductService;
        this.orderFacadeLockService = orderFacadeLockService;
    }

    @Override
    public OrderResponse.OrderDTO createOrder(OrderRequest.OrderCreate orderRequest) throws Exception {
        // 0. product_option_id 오름차순으로 정렬
        List<Long> requestProductOptionIds = orderRequest.productOptionIds().stream().sorted().toList();

        // 1. 옵션 ID 별로 등장 횟수 세기(세면서 정렬)
        Map<Long, Long> optionIdCountMap = requestProductOptionIds.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        return orderFacadeLockService.createOrderWithLock(orderRequest,optionIdCountMap);
    }

    @Override
    public OrderResponse.OrderDTO cancelOrder(OrderRequest.OrderCancel orderRequest) throws Exception {
        long requestOrderId = orderRequest.orderId();

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

        return orderFacadeLockService.cancelOrderWithLock(orderRequest,optionIdToQuantityMap);
    }
}
