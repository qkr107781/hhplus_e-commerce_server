package kr.hhplus.be.server.presentation.order;

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
    public ResponseEntity<OrderResponse.Create> orderCreate(@RequestBody OrderRequest.Create request){
        return ResponseEntity.ok(OrderResponse.Create.from(dummyDataUtil.getOrderCreate()));
    }

}