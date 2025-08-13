package kr.hhplus.be.server.presentation.order;

import kr.hhplus.be.server.application.order.dto.OrderRequest;
import kr.hhplus.be.server.application.order.dto.OrderResponse;
import kr.hhplus.be.server.application.order.service.OrderUseCase;
import kr.hhplus.be.server.swagger.OrderApiSpec;
import kr.hhplus.be.server.util.DummyDataUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class OrderController implements OrderApiSpec {

    private final DummyDataUtil dummyDataUtil;
    private final OrderUseCase orderUseCase;

    public OrderController(DummyDataUtil dummyDataUtil, OrderUseCase orderUseCase) {
        this.dummyDataUtil = dummyDataUtil;
        this.orderUseCase = orderUseCase;
    }

    @PostMapping("/order")
    @Override
    public ResponseEntity<OrderResponse.OrderDTO> orderCreate(@RequestBody OrderRequest.OrderCreate request) throws Exception {
        return ResponseEntity.ok(orderUseCase.createOrder(request));
    }

    @PatchMapping("/order")
    @Override
    public ResponseEntity<OrderResponse.OrderDTO> orderCancel(@RequestBody OrderRequest.OrderCancel request) throws Exception {
        return ResponseEntity.ok(orderUseCase.cancelOrder(request));
    }

}