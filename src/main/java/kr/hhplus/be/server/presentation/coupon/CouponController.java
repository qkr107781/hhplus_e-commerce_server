package kr.hhplus.be.server.presentation.coupon;

import kr.hhplus.be.server.application.coupon.dto.CouponRequest;
import kr.hhplus.be.server.application.coupon.dto.CouponResponse;
import kr.hhplus.be.server.application.coupon.service.CouponUseCase;
import kr.hhplus.be.server.swagger.CouponApiSpec;
import kr.hhplus.be.server.util.DummyDataUtil;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class CouponController implements CouponApiSpec {

    private final CouponUseCase couponUseCase;
    private final DummyDataUtil dummyDataUtil;

    public CouponController(CouponUseCase couponUseCase, DummyDataUtil dummyDataUtil) {
        this.couponUseCase = couponUseCase;
        this.dummyDataUtil = dummyDataUtil;
    }

    @PostMapping("/coupon/issue")
    @Override
    public ResponseEntity<CouponResponse.Issue> issue(@RequestBody CouponRequest.Issue request) throws Exception {
//        return ResponseEntity.ok(dummyDataUtil.getCouponIssue());
        return ResponseEntity.ok(couponUseCase.issuingCoupon(request.couponId(),request.userId()));
    }

    @GetMapping("/coupons/user/{userId}") // user_id -> userId
    @Override
    public ResponseEntity<CouponResponse.SelectByUserId> selectByUserId(@PathVariable long userId){ // user_id -> userId
//        return ResponseEntity.ok(dummyDataUtil.getCouponSelectByUserId());
        return ResponseEntity.ok(couponUseCase.selectCouponByUserId(userId));
    }

    @GetMapping("/coupons/{status}")
    public ResponseEntity<CouponResponse.SelectByStatus> selectByStatus(@PathVariable String status){
//        return ResponseEntity.ok(dummyDataUtil.getCouponSelectByStatus(status));
        return ResponseEntity.ok(couponUseCase.selectCouponByStatus(status));
    }
}