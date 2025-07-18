package kr.hhplus.be.server.presentation.coupon;

import java.time.LocalDateTime;

public class CouponResponse {

    public record Issue(
        long coupon_issued_id,
        long coupon_id,
        String coupon_name,
        long discount_price,
        long min_use_price,
        LocalDateTime issued_at,
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
        long coupon_id,
        String coupon_name,
        long discount_price,
        long min_use_price,
        LocalDateTime issued_at,
        LocalDateTime end_date,
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
        long coupon_id,
        String coupon_name,
        long discount_price,
        long total_coupon_amount,
        long remaining_coupon_amount,
        long min_use_price,
        LocalDateTime issuance_start_time,
        LocalDateTime issuance_end_time,
        long use_limit_time,
        String coupon_status,
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
