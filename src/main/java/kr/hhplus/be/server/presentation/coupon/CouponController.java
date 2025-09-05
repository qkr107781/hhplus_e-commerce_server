package kr.hhplus.be.server.presentation.coupon;

import kr.hhplus.be.server.application.coupon.dto.CouponRequest;
import kr.hhplus.be.server.application.coupon.dto.CouponResponse;
import kr.hhplus.be.server.application.coupon.service.CouponUseCase;
import kr.hhplus.be.server.swagger.CouponApiSpec;
import kr.hhplus.be.server.util.DummyDataUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping("/coupon/issue/async")
    @Override
    public ResponseEntity<String> issueAsync(@RequestBody CouponRequest.Issue request) throws Exception {
        return ResponseEntity.ok(couponUseCase.issuingCouponAsync(request.couponId(),request.userId()));
    }

    @PostMapping("/coupon/issue/kafka")
    @Override
    public ResponseEntity<String> issueKafka(@RequestBody CouponRequest.Issue request) throws Exception {
        return ResponseEntity.ok(couponUseCase.issuingCouponKafka(request.couponId(),request.userId()));
    }

    @GetMapping("/coupons/user/{userId}") // user_id -> userId
    @Override
    public ResponseEntity<List<CouponResponse.SelectByUserId>> selectByUserId(@PathVariable long userId){ // user_id -> userId
//        return ResponseEntity.ok(dummyDataUtil.getCouponSelectByUserId());
        return ResponseEntity.ok(couponUseCase.selectCouponByUserId(userId));
    }

    @GetMapping("/coupons/{status}")
    public ResponseEntity<List<CouponResponse.SelectByStatus>> selectByStatus(@PathVariable String status){
//        return ResponseEntity.ok(dummyDataUtil.getCouponSelectByStatus(status));
        return ResponseEntity.ok(couponUseCase.selectCouponByStatus(status));
    }
}