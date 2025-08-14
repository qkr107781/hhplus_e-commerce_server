package kr.hhplus.be.server.application.coupon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponIssuedInfo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
            @Schema(description = "발급일", requiredMode = Schema.RequiredMode.REQUIRED)
            LocalDateTime issuedAt,
            @Schema(description = "만료일", requiredMode = Schema.RequiredMode.REQUIRED)
            LocalDateTime endDate
    ) {
        public static Issue from(CouponIssuedInfo couponIssuedInfo,Coupon coupon) {
            return new Issue(couponIssuedInfo.getCouponIssuedId(),
                    coupon.getCouponId(),
                    coupon.getCouponName(),
                    coupon.getDiscountPrice(),
                    couponIssuedInfo.getIssuedAt(),
                    couponIssuedInfo.getEndDate());
        }
    }
    public record SelectByUserId(
            @Schema(description = "쿠폰 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            long couponId,
            @Schema(description = "쿠폰명", requiredMode = Schema.RequiredMode.REQUIRED)
            String couponName,
            @Schema(description = "할인금액", requiredMode = Schema.RequiredMode.REQUIRED)
            long discountPrice,
            @Schema(description = "발급일", requiredMode = Schema.RequiredMode.REQUIRED)
            LocalDateTime issuedAt,
            @Schema(description = "만료일", requiredMode = Schema.RequiredMode.REQUIRED)
            LocalDateTime endDate,
            @Schema(description = "사용여부", requiredMode = Schema.RequiredMode.REQUIRED)
            String useYn
    ){
        public static List<SelectByUserId> from(List<CouponIssuedInfo> couponIssuedInfoList, List<Coupon> couponList){
            // CouponList를 couponId를 키로 하는 Map으로 변환하여 O(1) 시간 복잡도로 조회 가능하게 합니다.
            Map<Long, Coupon> couponMap = couponList.stream()
                    .collect(Collectors.toMap(Coupon::getCouponId, coupon -> coupon));

            // couponIssuedInfoList를 순회하며 각 정보를 SelectByUserId 객체로 변환합니다.
            return couponIssuedInfoList.stream()
                    .map(couponIssuedInfo -> {
                        // CouponIssuedInfo의 couponId를 사용하여 Coupon 정보를 찾습니다.
                        Coupon coupon = couponMap.get(couponIssuedInfo.getCouponId());

                        // 만약 매칭되는 Coupon 정보가 없으면 null을 반환하거나 예외 처리할 수 있습니다.
                        if (coupon == null) {
                            // TODO: 예외 처리 또는 로깅 로직 추가
                            return null; // 또는 throw new IllegalStateException("Matching coupon not found.");
                        }

                        // 찾은 정보들을 기반으로 SelectByUserId 레코드를 생성하여 반환합니다.
                        return new SelectByUserId(
                                coupon.getCouponId(),
                                coupon.getCouponName(),
                                coupon.getDiscountPrice(),
                                couponIssuedInfo.getIssuedAt(),
                                couponIssuedInfo.getEndDate(),
                                couponIssuedInfo.getUseYn());
                    })
                    .collect(Collectors.toList());
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
            @Schema(description = "발급 시작 시간", requiredMode = Schema.RequiredMode.REQUIRED)
            LocalDateTime issuanceStartTime,
            @Schema(description = "발급 종료 시간", requiredMode = Schema.RequiredMode.REQUIRED)
            LocalDateTime issuanceEndTime,
            @Schema(description = "쿠폰 발급 진행 상태", requiredMode = Schema.RequiredMode.REQUIRED)
            String couponStatus,
            @Schema(description = "등록일", requiredMode = Schema.RequiredMode.REQUIRED)
            LocalDateTime regDate
    ){
        public static List<SelectByStatus> from(List<Coupon> couponList){
            return couponList.stream()
                    .map(coupon -> new SelectByStatus(
                            coupon.getCouponId(),
                            coupon.getCouponName(),
                            coupon.getDiscountPrice(),
                            coupon.getTotalCouponAmount(),
                            coupon.getRemainingCouponAmount(),
                            coupon.getIssuanceStartTime(),
                            coupon.getIssuanceEndTime(),
                            coupon.getCouponStatus(),
                            coupon.getRegDate()))
                    // 매핑된 SelectByStatus 객체들을 다시 List로 수집합니다.
                    .collect(Collectors.toList());
        }
    }
}