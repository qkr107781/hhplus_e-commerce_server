package kr.hhplus.be.server.presentation.balance;

import io.swagger.v3.oas.annotations.media.Schema;

public class BalanceResponse {
    public record Charge(
        @Schema(description = "사용자ID", requiredMode = Schema.RequiredMode.REQUIRED)
        long user_id,
        @Schema(description = "충전금액", requiredMode = Schema.RequiredMode.REQUIRED)
        long charge_amount,
        @Schema(description = "잔액", requiredMode = Schema.RequiredMode.REQUIRED)
        long balance
    ){
        public static BalanceResponse.Charge from(BalanceResponse.Charge charge){
            return new BalanceResponse.Charge(charge.user_id,charge.charge_amount,charge.balance);
        }
    }

    public record SelectBalanceByUserId(
            @Schema(description = "사용자ID", requiredMode = Schema.RequiredMode.REQUIRED)
            long user_id,
            @Schema(description = "잔액", requiredMode = Schema.RequiredMode.REQUIRED)
            long balance
    ){
        public static BalanceResponse.SelectBalanceByUserId from(BalanceResponse.SelectBalanceByUserId charge){
            return new BalanceResponse.SelectBalanceByUserId(charge.user_id,charge.balance);
        }
    }
}
