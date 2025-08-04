package kr.hhplus.be.server.unit.application.order.service;

import kr.hhplus.be.server.application.order.repository.OrderRepository;
import kr.hhplus.be.server.application.order.service.OrderService;
import kr.hhplus.be.server.domain.order.Order;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    OrderRepository orderRepository;

    @Test
    @DisplayName("[주문 취소]")
    void cancelOrder(){
        //Given
        Order order = Order.builder()
                .orderId(1L)
                .userId(1L)
                .couponId(1L)
                .orderStatus("pending_payment")
                .build();

        Order afterCancelOrder = Order.builder()
                .orderId(1L)
                .userId(1L)
                .couponId(1L)
                .orderStatus("cancel_order")
                .build();

        when(orderRepository.findByOrderId(1L)).thenReturn(order);
        when(orderRepository.save(order)).thenReturn(afterCancelOrder);

        //When
        OrderService orderService = new OrderService(orderRepository);
        Order result = orderService.cancelOrder(1L);

        //Then
        assertEquals("cancel_order",result.getOrderStatus());

    }
}
