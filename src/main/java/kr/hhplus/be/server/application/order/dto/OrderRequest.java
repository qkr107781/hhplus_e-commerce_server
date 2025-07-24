package kr.hhplus.be.server.application.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public class OrderRequest {
    public record OrderCreate(
            @Schema(description = "사용자 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            long userId,
            @Schema(description = "주문 상품 옵션 ID 목록", requiredMode = Schema.RequiredMode.REQUIRED)
            List<Long> productOptionIds,
            @Schema(description = "쿠폰 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            long couponId
    ){
    }
}