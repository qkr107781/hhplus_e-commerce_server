package kr.hhplus.be.server.presentation.coupon;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public class CouponResponse {

    public record Issue(
        @Schema(description = "쿠폰 발급 정보 ID", requiredMode = Schema.RequiredMode.REQUIRED)
        long coupon_issued_id,
        @Schema(description = "쿠폰 ID", requiredMode = Schema.RequiredMode.REQUIRED)
        long coupon_id,
        @Schema(description = "쿠폰명", requiredMode = Schema.RequiredMode.REQUIRED)
        String coupon_name,
        @Schema(description = "할인금액", requiredMode = Schema.RequiredMode.REQUIRED)
        long discount_price,
        @Schema(description = "사용 최소 금액 제한", requiredMode = Schema.RequiredMode.REQUIRED)
        long min_use_price,
        @Schema(description = "발급일", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDateTime issued_at,
        @Schema(description = "만료일", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDateTime end_date
    ) {
        public static Issue from(Issue issue) {
            return new Issue(issue.coupon_issued_id,
                    issue.coupon_id,
                    issue.coupon_name,
                    issue.discount_price,
                    issue.min_use_price,
                    issue.issued_at,
                    issue.end_date);
        }
    }
    public record SelectByUserId(
        @Schema(description = "쿠폰 ID", requiredMode = Schema.RequiredMode.REQUIRED)
        long coupon_id,
        @Schema(description = "쿠폰명", requiredMode = Schema.RequiredMode.REQUIRED)
        String coupon_name,
        @Schema(description = "할인금액", requiredMode = Schema.RequiredMode.REQUIRED)
        long discount_price,
        @Schema(description = "사용 최소 금액 제한", requiredMode = Schema.RequiredMode.REQUIRED)
        long min_use_price,
        @Schema(description = "발급일", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDateTime issued_at,
        @Schema(description = "만료일", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDateTime end_date,
        @Schema(description = "사용여부", requiredMode = Schema.RequiredMode.REQUIRED)
        String use_yn
    ){
        public static SelectByUserId from(SelectByUserId selectByUserId){
            return new SelectByUserId(selectByUserId.coupon_id,
                                        selectByUserId.coupon_name,
                                        selectByUserId.discount_price,
                                        selectByUserId.min_use_price,
                                        selectByUserId.issued_at,
                                        selectByUserId.end_date,
                                        selectByUserId.use_yn);
        }
    }
    public record SelectByStatus(
        @Schema(description = "쿠폰 ID", requiredMode = Schema.RequiredMode.REQUIRED)
        long coupon_id,
        @Schema(description = "쿠폰명", requiredMode = Schema.RequiredMode.REQUIRED)
        String coupon_name,
        @Schema(description = "할인금액", requiredMode = Schema.RequiredMode.REQUIRED)
        long discount_price,
        @Schema(description = "전체 갯수", requiredMode = Schema.RequiredMode.REQUIRED)
        long total_coupon_amount,
        @Schema(description = "잔여 갯수", requiredMode = Schema.RequiredMode.REQUIRED)
        long remaining_coupon_amount,
        @Schema(description = "사용 최소 금액 제한", requiredMode = Schema.RequiredMode.REQUIRED)
        long min_use_price,
        @Schema(description = "발급 시작 시간", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDateTime issuance_start_time,
        @Schema(description = "발급 종료 시간", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDateTime issuance_end_time,
        @Schema(description = "발급 후 사용 제한 시간", requiredMode = Schema.RequiredMode.REQUIRED)
        long use_limit_time,
        @Schema(description = "쿠폰 발급 진행 상태", requiredMode = Schema.RequiredMode.REQUIRED)
        String coupon_status,
        @Schema(description = "등록일", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDateTime reg_date
    ){
        public static SelectByStatus from(SelectByStatus selectByStatus){
            return new SelectByStatus(selectByStatus.coupon_id,
                                        selectByStatus.coupon_name,
                                        selectByStatus.discount_price,
                                        selectByStatus.total_coupon_amount,
                                        selectByStatus.remaining_coupon_amount,
                                        selectByStatus.min_use_price,
                                        selectByStatus.issuance_start_time,
                                        selectByStatus.issuance_end_time,
                                        selectByStatus.use_limit_time,
                                        selectByStatus.coupon_status,
                                        selectByStatus.reg_date);
        }
    }
}
