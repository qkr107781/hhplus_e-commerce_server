package kr.hhplus.be.server.persistence.order;

import kr.hhplus.be.server.application.order.repository.OrderProductRepository;
import kr.hhplus.be.server.domain.order.OrderProduct;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderProductAdapter implements OrderProductRepository {
    private final OrderProductJpaRepository orderProductJpaRepository;

    public OrderProductAdapter(OrderProductJpaRepository orderProductJpaRepository) {
        this.orderProductJpaRepository = orderProductJpaRepository;
    }


    @Override
    public List<OrderProduct> findByOrderIdOrderByProductOptionIdAsc(long orderId) {
        return orderProductJpaRepository.findByOrderIdOrderByProductOptionIdAsc(orderId);
    }

    @Override
    public OrderProduct save(OrderProduct orderProduct) {
        return orderProductJpaRepository.save(orderProduct);
    }

    @Override
    public OrderProduct findByOrderIdAndProductOptionId(long requestOrderId,long productOptionId) {
        return orderProductJpaRepository.findByOrderIdAndProductOptionId(requestOrderId,productOptionId);
    }
}
