package kr.hhplus.be.server.application.order.service;

import kr.hhplus.be.server.application.order.dto.OrderRequest;
import kr.hhplus.be.server.application.order.dto.OrderResponse;

public interface OrderUseCase {

    OrderResponse.OrderDTO createOrder(OrderRequest.OrderCreate orderRequest) throws Exception;

    OrderResponse.OrderDTO cancelOrder(OrderRequest.OrderCancel orderRequest) throws Exception;
}
