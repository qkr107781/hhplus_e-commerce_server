package kr.hhplus.be.server.persistence.order;

import kr.hhplus.be.server.domain.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderJpaRepository extends JpaRepository<Order,Long> {

    // Order와 OrderProduct를 한 번의 쿼리로 함께 가져오는 쿼리 (N+1 문제 방지)
    @Query("SELECT o FROM Order o JOIN FETCH o.orderProducts op WHERE o.orderId = :orderId")
    Order findByIdWithOrderProducts(@Param("orderId") long orderId);
}
