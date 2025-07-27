package kr.hhplus.be.server.persistence.order;

import kr.hhplus.be.server.domain.order.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderProductJpaRepository extends JpaRepository<OrderProduct,Long> {
}
