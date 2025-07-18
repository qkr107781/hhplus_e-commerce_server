package kr.hhplus.be.server.presentation.coupon;

public class CouponRequest {
    public record Issue(
        long user_id,
        long coupon_id
    ){
    }
}
