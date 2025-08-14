package kr.hhplus.be.server.persistence.order;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderJpaRepository extends JpaRepository<Order,Long> {

    Order findByOrderId(long orderId);

    @Lock(LockModeType.OPTIMISTIC)
    Order findById(long orderId);

    List<Order> findByOrderStatusAndOrderDateBetween(String orderStatus, LocalDateTime startDate, LocalDateTime endDate);
}
