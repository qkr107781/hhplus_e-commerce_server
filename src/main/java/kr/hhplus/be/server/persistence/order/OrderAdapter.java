package kr.hhplus.be.server.persistence.order;

import kr.hhplus.be.server.application.order.repository.OrderRepository;
import kr.hhplus.be.server.domain.order.Order;

public class OrderAdapter implements OrderRepository {

    private final OrderRepository orderRepository;

    public OrderAdapter(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Order save(Order order) {
        return orderRepository.save(order);
    }

    @Override
    public Order findByOrderId(long orderId) {
        return orderRepository.findByOrderId(orderId);
    }
}
