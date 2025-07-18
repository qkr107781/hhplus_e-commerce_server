package kr.hhplus.be.server.presentation.payment;

import kr.hhplus.be.server.util.DummyDataUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class PaymentController {

    private final DummyDataUtil dummyDataUtil;

    public PaymentController(DummyDataUtil dummyDataUtil) {
        this.dummyDataUtil = dummyDataUtil;
    }

    @PostMapping("/payment")
    public ResponseEntity<PaymentResponse.Create> paymentCreate(@RequestBody PaymentRequest.Create request){
        return ResponseEntity.ok(PaymentResponse.Create.from(dummyDataUtil.getPaymentCreate()));
    }
}
