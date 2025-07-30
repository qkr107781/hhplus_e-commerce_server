package kr.hhplus.be.server.application.order.service;

import kr.hhplus.be.server.application.order.repository.OrderProductRepository;
import kr.hhplus.be.server.domain.order.OrderProduct;
import org.springframework.stereotype.Service;

import java.util.List;

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
}