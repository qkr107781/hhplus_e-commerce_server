package kr.hhplus.be.server.application.balance.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record BalanceRequest(
    @Schema(description = "사용자 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    long userId,
    @Schema(description = "충전금액", requiredMode = Schema.RequiredMode.REQUIRED)
    long chargeAmount
){
}
