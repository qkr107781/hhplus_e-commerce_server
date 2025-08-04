package kr.hhplus.be.server.application.balance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.domain.balance.Balance;

import java.time.LocalDateTime;

public record BalanceResponse(
        @Schema(description = "사용자 ID", requiredMode = Schema.RequiredMode.REQUIRED)
        long userId,
        @Schema(description = "잔액", requiredMode = Schema.RequiredMode.REQUIRED)
        long balance,
        @Schema(description = "마지막 충전일", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDateTime lastChargeDate
){
    public static BalanceResponse from(Balance balance){
        return new BalanceResponse(balance.getUserId(), balance.getBalance(), balance.getLastChargeDate());
    }
}