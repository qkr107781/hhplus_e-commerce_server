package kr.hhplus.be.server.application.order.repository;

import kr.hhplus.be.server.domain.order.Order;

import java.time.LocalDate;
import java.util.List;

public interface OrderRepository {

    Order save(Order order);

    Order findByOrderId(long orderId);

    Order findById(long orderId);

    List<Order> findByOrderStatusAndOrderDateBetween(String orderStatus, LocalDate startDate, LocalDate endDate);

}
