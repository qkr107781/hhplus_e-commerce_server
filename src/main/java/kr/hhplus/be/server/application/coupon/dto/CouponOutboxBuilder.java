package kr.hhplus.be.server.application.coupon.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public class CouponOutboxBuilder {
    public record Create(
            @Schema(description = "사용자 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            long userId,
            @Schema(description = "쿠폰 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            long couponId,
            @Schema(description = "진행 상태", requiredMode = Schema.RequiredMode.REQUIRED)
            String status,
            @Schema(description = "요청일", requiredMode = Schema.RequiredMode.REQUIRED)
            LocalDateTime createdAt,
            @Schema(description = "카프카 토픽 키", requiredMode = Schema.RequiredMode.REQUIRED)
            String topicKey,
            @Schema(description = "카프카 토픽 값", requiredMode = Schema.RequiredMode.REQUIRED)
            String payload

    ){
        public static kr.hhplus.be.server.domain.coupon.CouponOutbox toDomain(CouponOutboxBuilder.Create requestCouponOutbox){
            return kr.hhplus.be.server.domain.coupon.CouponOutbox.builder()
                    .userId(requestCouponOutbox.userId())
                    .couponId(requestCouponOutbox.couponId())
                    .status(requestCouponOutbox.status())
                    .createdAt(requestCouponOutbox.createdAt())
                    .topicKey(requestCouponOutbox.topicKey())
                    .payload(requestCouponOutbox.payload())
                    .build();
        }

    }
    public record Update(
            @Schema(description = "Outbox ID", requiredMode = Schema.RequiredMode.REQUIRED)
            long outboxId,
            @Schema(description = "사용자 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            long userId,
            @Schema(description = "쿠폰 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            long couponId,
            @Schema(description = "진행 상태", requiredMode = Schema.RequiredMode.REQUIRED)
            String status

    ){
        public static kr.hhplus.be.server.domain.coupon.CouponOutbox toDomain(CouponOutboxBuilder.Update requestCouponOutbox){
            return kr.hhplus.be.server.domain.coupon.CouponOutbox.builder()
                    .userId(requestCouponOutbox.userId())
                    .couponId(requestCouponOutbox.couponId())
                    .status(requestCouponOutbox.status())
                    .build();
        }

    }
}
