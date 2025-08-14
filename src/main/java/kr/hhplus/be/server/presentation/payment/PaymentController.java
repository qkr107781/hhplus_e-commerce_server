package kr.hhplus.be.server.presentation.payment;

import kr.hhplus.be.server.application.payment.dto.PaymentRequest;
import kr.hhplus.be.server.application.payment.dto.PaymentResponse;
import kr.hhplus.be.server.application.payment.service.PaymentUseCase;
import kr.hhplus.be.server.swagger.PaymentApiSpec;
import kr.hhplus.be.server.util.DummyDataUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class PaymentController implements PaymentApiSpec {

    private final DummyDataUtil dummyDataUtil;
    private final PaymentUseCase paymentUseCase;

    public PaymentController(DummyDataUtil dummyDataUtil, PaymentUseCase paymentUseCase) {
        this.dummyDataUtil = dummyDataUtil;
        this.paymentUseCase = paymentUseCase;
    }

    @PostMapping("/payment")
    @Override
    public ResponseEntity<PaymentResponse.Create> paymentCreate(@RequestBody PaymentRequest.Create request) throws Exception {
        return ResponseEntity.ok(paymentUseCase.createPayment(request));
    }
}