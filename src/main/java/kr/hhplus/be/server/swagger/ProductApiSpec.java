package kr.hhplus.be.server.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.application.product.dto.ProductResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "상품", description = "상품 관련 API")
public interface ProductApiSpec {

    @Operation(summary = "결제")
    ResponseEntity<List<ProductResponse.Select>> productSelect();

    @Operation(summary = "결제 통계 조회")
    ResponseEntity<List<ProductResponse.Statistics>> productStatistics();
}
