package kr.hhplus.be.server.persistence.order;

import kr.hhplus.be.server.application.order.repository.OrderRepository;
import kr.hhplus.be.server.domain.order.Order;
import org.springframework.stereotype.Component;

@Component
public class OrderAdapter implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;

    public OrderAdapter(OrderJpaRepository orderJpaRepository) {
        this.orderJpaRepository = orderJpaRepository;
    }

    @Override
    public Order save(Order order) {
        return orderJpaRepository.save(order);
    }

    @Override
    public Order findByOrderId(long orderId) {
        return orderJpaRepository.findByOrderId(orderId);
    }

}
