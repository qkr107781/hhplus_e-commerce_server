package kr.hhplus.be.server.application.order.repository;

import kr.hhplus.be.server.domain.order.Order;

public interface OrderRepository {

    Order save(Order order);

    Order findByOrderId(long orderId);

}
