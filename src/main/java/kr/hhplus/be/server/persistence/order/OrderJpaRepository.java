package kr.hhplus.be.server.persistence.order;

import kr.hhplus.be.server.domain.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderJpaRepository extends JpaRepository<Order,Long> {

    Order findByOrderId(long orderId);

    List<Order> findByOrderStatusAndOrderDateBetween(String orderStatus, LocalDateTime startDate, LocalDateTime endDate);
}
