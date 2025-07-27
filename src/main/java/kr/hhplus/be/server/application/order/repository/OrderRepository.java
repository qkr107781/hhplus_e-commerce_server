package kr.hhplus.be.server.application.order.repository;

import kr.hhplus.be.server.domain.order.Order;

public interface OrderRepository {

    Order save(Order order);

    // Order와 OrderProduct를 한 번의 쿼리로 함께 가져오는 쿼리 (N+1 문제 방지)
    Order findByIdWithOrderProducts(long orderId);

}
