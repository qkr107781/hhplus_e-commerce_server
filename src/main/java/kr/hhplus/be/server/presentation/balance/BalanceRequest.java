package kr.hhplus.be.server.presentation.balance;

import io.swagger.v3.oas.annotations.media.Schema;

public class BalanceRequest {
    public record Charge(
        @Schema(description = "사용자ID", requiredMode = Schema.RequiredMode.REQUIRED)
        long user_id,
        @Schema(description = "충전금액", requiredMode = Schema.RequiredMode.REQUIRED)
        long charge_amount
    ){
    }
}
