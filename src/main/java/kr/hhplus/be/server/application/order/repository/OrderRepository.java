package kr.hhplus.be.server.application.order.repository;

import kr.hhplus.be.server.domain.order.Order;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository {

    Order save(Order order);

    Order findByOrderId(long orderId);

}
