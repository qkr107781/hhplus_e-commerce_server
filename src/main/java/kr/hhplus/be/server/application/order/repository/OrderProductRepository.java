package kr.hhplus.be.server.application.order.repository;

import kr.hhplus.be.server.domain.order.OrderProduct;

import java.util.List;

public interface OrderProductRepository {

    List<OrderProduct> findByOrderId(long orderId);

    OrderProduct save(OrderProduct orderProduct);

    OrderProduct findByOrderIdAndProductOptionId(long requestOrderId, long productOptionId);

}
