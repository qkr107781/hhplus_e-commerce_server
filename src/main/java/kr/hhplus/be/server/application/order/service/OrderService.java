package kr.hhplus.be.server.application.order.service;

import kr.hhplus.be.server.application.order.repository.OrderRepository;
import kr.hhplus.be.server.domain.order.Order;
import org.springframework.stereotype.Service;


@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order selectOrderByOrderId(long orderId){
        return orderRepository.findByOrderId(orderId);
    }

    public Order createOrder(Order createOrder){
        return orderRepository.save(createOrder);
    }

    public void updateOrderStatusToPayment(Order order){
        order.updateOrderStatusToPayment();
    }

}
