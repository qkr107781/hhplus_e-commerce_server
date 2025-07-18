package kr.hhplus.be.server.presentation.balance;

import io.swagger.v3.oas.annotations.media.Schema;

public class BalanceResponse {
    public record Charge(
            @Schema(description = "사용자ID", requiredMode = Schema.RequiredMode.REQUIRED)
            long userId,
            @Schema(description = "충전금액", requiredMode = Schema.RequiredMode.REQUIRED)
            long chargeAmount,
            @Schema(description = "잔액", requiredMode = Schema.RequiredMode.REQUIRED)
            long balance
    ){
        public static BalanceResponse.Charge from(BalanceResponse.Charge charge){
            return new BalanceResponse.Charge(charge.userId, charge.chargeAmount, charge.balance);
        }
    }

    public record SelectBalanceByUserId(
            @Schema(description = "사용자ID", requiredMode = Schema.RequiredMode.REQUIRED)
            long userId,
            @Schema(description = "잔액", requiredMode = Schema.RequiredMode.REQUIRED)
            long balance
    ){
        public static BalanceResponse.SelectBalanceByUserId from(BalanceResponse.SelectBalanceByUserId charge){
            return new BalanceResponse.SelectBalanceByUserId(charge.userId, charge.balance);
        }
    }
}