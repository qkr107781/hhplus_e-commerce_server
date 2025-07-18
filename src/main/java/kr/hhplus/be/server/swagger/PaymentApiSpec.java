package kr.hhplus.be.server.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.presentation.payment.PaymentRequest;
import kr.hhplus.be.server.presentation.payment.PaymentResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "결제", description = "결제 관련 API")
public interface PaymentApiSpec {

    @Operation(summary = "결제")
    ResponseEntity<PaymentResponse.Create> paymentCreate(PaymentRequest.Create request);
}
