package kr.hhplus.be.server.application.order.service;

import kr.hhplus.be.server.application.order.repository.OrderProductRepository;
import kr.hhplus.be.server.application.order.repository.OrderRepository;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderProduct;
import org.springframework.stereotype.Service;


@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;

    public OrderService(OrderRepository orderRepository,OrderProductRepository orderProductRepository) {
        this.orderRepository = orderRepository;
        this.orderProductRepository = orderProductRepository;
    }

    public Order createOrder(Order requestOrder){
        return orderRepository.save(requestOrder);
    }

    public OrderProduct createOrderProduct(OrderProduct requestOrderProduct){
        return orderProductRepository.save(requestOrderProduct);
    }

}
