package kr.hhplus.be.server.application.order.service;

import kr.hhplus.be.server.application.order.dto.OrderProductSummary;
import kr.hhplus.be.server.application.order.repository.OrderProductRepository;
import kr.hhplus.be.server.domain.order.OrderProduct;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderProductService {

    private final OrderProductRepository orderProductRepository;

    public OrderProductService(OrderProductRepository orderProductRepository) {
        this.orderProductRepository = orderProductRepository;
    }

    public List<OrderProduct> selectOrderProductsByOrderId(long orderId){
        return orderProductRepository.findByOrderId(orderId);
    }

    public OrderProduct createOrderProduct(OrderProduct createOrderProduct){
        return orderProductRepository.save(createOrderProduct);
    }

    public OrderProduct selectOrderProductByOrderIdAndProductOptionId(long requestOrderId, long productOptionId){
        return orderProductRepository.findByOrderIdAndProductOptionId(requestOrderId,productOptionId);
    }

    public List<OrderProductSummary> getTop5OrderProduct(List<OrderProduct> orderProductList){
        if (orderProductList == null || orderProductList.isEmpty()) {
            return List.of(); // 빈 리스트 또는 null이 들어오면 빈 결과 반환
        }

        // Java Stream API를 사용하여 productOptionId를 기준으로 그룹화하고 수량을 집계
        Map<Long, Long> groupedQuantities = orderProductList.stream()
                .collect(Collectors.groupingBy(
                        OrderProduct::getProductOptionId, // productOptionId를 그룹화 기준으로 설정
                        Collectors.summingLong(OrderProduct::getProductQuantity) // 각 그룹의 quantity를 합산
                ));

        // 집계된 Map을 ProductOptionSummary DTO 리스트로 변환
        return groupedQuantities.entrySet().stream()
                .map(entry -> new OrderProductSummary(entry.getKey(), entry.getValue()))
                // 1. totalQuantity (집계된 수량) 기준으로 내림차순 정렬
                .sorted(Comparator.comparing(OrderProductSummary::totalOrderedQuantity, Comparator.reverseOrder()))
                // 2. 상위 5개만 자르기
                .limit(5)
                .collect(Collectors.toList());
    }
}