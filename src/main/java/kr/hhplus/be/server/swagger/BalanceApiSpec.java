package kr.hhplus.be.server.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.presentation.balance.BalanceRequest;
import kr.hhplus.be.server.presentation.balance.BalanceResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "잔액", description = "잔액 관련 API")
public interface BalanceApiSpec {
    @Operation(summary = "잔액 충전")
    ResponseEntity<BalanceResponse.Charge> charge(BalanceRequest.Charge charge);

    @Operation(summary = "잔액 조회")
    ResponseEntity<BalanceResponse.SelectBalanceByUserId> selectBalanceByUserId(long user_id);
}