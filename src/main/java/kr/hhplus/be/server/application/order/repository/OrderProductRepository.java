package kr.hhplus.be.server.application.order.repository;

import kr.hhplus.be.server.domain.order.OrderProduct;

public interface OrderProductRepository {

    OrderProduct save(OrderProduct orderProduct);

}
