package kr.hhplus.be.server.persistence.order;

import kr.hhplus.be.server.application.order.repository.OrderRepository;
import kr.hhplus.be.server.domain.order.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

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

    @Override
    public Order findById(long orderId) {
        return orderJpaRepository.findById(orderId);
    }

    @Override
    public List<Order> findByOrderStatusAndOrderDateBetween(String orderStatus, LocalDate startDate, LocalDate endDate) {
        return orderJpaRepository.findByOrderStatusAndOrderDateBetween(orderStatus,startDate.atStartOfDay(),endDate.atStartOfDay());
    }

}
