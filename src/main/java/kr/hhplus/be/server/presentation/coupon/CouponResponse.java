package kr.hhplus.be.server.presentation.coupon;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public class CouponResponse {

    public record Issue(
            @Schema(description = "쿠폰 발급 정보 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            long couponIssuedId,
            @Schema(description = "쿠폰 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            long couponId,
            @Schema(description = "쿠폰명", requiredMode = Schema.RequiredMode.REQUIRED)
            String couponName,
            @Schema(description = "할인금액", requiredMode = Schema.RequiredMode.REQUIRED)
            long discountPrice,
            @Schema(description = "사용 최소 금액 제한", requiredMode = Schema.RequiredMode.REQUIRED)
            long minUsePrice,
            @Schema(description = "발급일", requiredMode = Schema.RequiredMode.REQUIRED)
            LocalDateTime issuedAt,
            @Schema(description = "만료일", requiredMode = Schema.RequiredMode.REQUIRED)
            LocalDateTime endDate
    ) {
        public static Issue from(Issue issue) {
            return new Issue(issue.couponIssuedId,
                    issue.couponId,
                    issue.couponName,
                    issue.discountPrice,
                    issue.minUsePrice,
                    issue.issuedAt,
                    issue.endDate);
        }
    }
    public record SelectByUserId(
            @Schema(description = "쿠폰 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            long couponId,
            @Schema(description = "쿠폰명", requiredMode = Schema.RequiredMode.REQUIRED)
            String couponName,
            @Schema(description = "할인금액", requiredMode = Schema.RequiredMode.REQUIRED)
            long discountPrice,
            @Schema(description = "사용 최소 금액 제한", requiredMode = Schema.RequiredMode.REQUIRED)
            long minUsePrice,
            @Schema(description = "발급일", requiredMode = Schema.RequiredMode.REQUIRED)
            LocalDateTime issuedAt,
            @Schema(description = "만료일", requiredMode = Schema.RequiredMode.REQUIRED)
            LocalDateTime endDate,
            @Schema(description = "사용여부", requiredMode = Schema.RequiredMode.REQUIRED)
            String useYn
    ){
        public static SelectByUserId from(SelectByUserId selectByUserId){
            return new SelectByUserId(selectByUserId.couponId,
                    selectByUserId.couponName,
                    selectByUserId.discountPrice,
                    selectByUserId.minUsePrice,
                    selectByUserId.issuedAt,
                    selectByUserId.endDate,
                    selectByUserId.useYn);
        }
    }
    public record SelectByStatus(
            @Schema(description = "쿠폰 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            long couponId,
            @Schema(description = "쿠폰명", requiredMode = Schema.RequiredMode.REQUIRED)
            String couponName,
            @Schema(description = "할인금액", requiredMode = Schema.RequiredMode.REQUIRED)
            long discountPrice,
            @Schema(description = "전체 갯수", requiredMode = Schema.RequiredMode.REQUIRED)
            long totalCouponAmount,
            @Schema(description = "잔여 갯수", requiredMode = Schema.RequiredMode.REQUIRED)
            long remainingCouponAmount,
            @Schema(description = "사용 최소 금액 제한", requiredMode = Schema.RequiredMode.REQUIRED)
            long minUsePrice,
            @Schema(description = "발급 시작 시간", requiredMode = Schema.RequiredMode.REQUIRED)
            LocalDateTime issuanceStartTime,
            @Schema(description = "발급 종료 시간", requiredMode = Schema.RequiredMode.REQUIRED)
            LocalDateTime issuanceEndTime,
            @Schema(description = "발급 후 사용 제한 시간", requiredMode = Schema.RequiredMode.REQUIRED)
            long useLimitTime,
            @Schema(description = "쿠폰 발급 진행 상태", requiredMode = Schema.RequiredMode.REQUIRED)
            String couponStatus,
            @Schema(description = "등록일", requiredMode = Schema.RequiredMode.REQUIRED)
            LocalDateTime regDate
    ){
        public static SelectByStatus from(SelectByStatus selectByStatus){
            return new SelectByStatus(selectByStatus.couponId,
                    selectByStatus.couponName,
                    selectByStatus.discountPrice,
                    selectByStatus.totalCouponAmount,
                    selectByStatus.remainingCouponAmount,
                    selectByStatus.minUsePrice,
                    selectByStatus.issuanceStartTime,
                    selectByStatus.issuanceEndTime,
                    selectByStatus.useLimitTime,
                    selectByStatus.couponStatus,
                    selectByStatus.regDate);
        }
    }
}