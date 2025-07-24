package kr.hhplus.be.server.persistence.order;

import kr.hhplus.be.server.application.order.repository.OrderProductRepository;
import kr.hhplus.be.server.domain.order.OrderProduct;

public class OrderProductAdapter implements OrderProductRepository {
    private final OrderProductRepository orderProductRepository;

    public OrderProductAdapter(OrderProductRepository orderProductRepository) {
        this.orderProductRepository = orderProductRepository;
    }

    @Override
    public OrderProduct save(OrderProduct orderProduct) {
        return orderProductRepository.save(orderProduct);
    }
}
