package kr.hhplus.be.server.application.coupon.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class CouponRequest {
    public record Issue(
            @Schema(description = "사용자 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            long userId,
            @Schema(description = "쿠폰 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            long couponId
    ){
    }
}