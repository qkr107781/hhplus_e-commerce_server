package kr.hhplus.be.server.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.presentation.order.OrderRequest;
import kr.hhplus.be.server.presentation.order.OrderResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "주문", description = "주문 관련 API")
public interface OrderApiSpec {

    @Operation(summary = "주문")
    ResponseEntity<OrderResponse.Create> orderCreate( OrderRequest.Create request);
}
