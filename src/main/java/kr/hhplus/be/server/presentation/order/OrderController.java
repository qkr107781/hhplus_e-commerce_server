package kr.hhplus.be.server.presentation.order;

import kr.hhplus.be.server.application.order.dto.OrderRequest;
import kr.hhplus.be.server.application.order.dto.OrderResponse;
import kr.hhplus.be.server.swagger.OrderApiSpec;
import kr.hhplus.be.server.util.DummyDataUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class OrderController implements OrderApiSpec {

    private final DummyDataUtil dummyDataUtil;

    public OrderController(DummyDataUtil dummyDataUtil) {
        this.dummyDataUtil = dummyDataUtil;
    }

    @PostMapping("/order")
    @Override
    public ResponseEntity<OrderResponse.OrderDTO> orderCreate(@RequestBody OrderRequest.OrderCreate request){
        return ResponseEntity.ok(dummyDataUtil.getOrderCreate());
    }

}