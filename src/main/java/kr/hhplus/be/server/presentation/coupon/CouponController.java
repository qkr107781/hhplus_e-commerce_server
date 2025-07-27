package kr.hhplus.be.server.presentation.coupon;

import kr.hhplus.be.server.application.coupon.dto.CouponRequest;
import kr.hhplus.be.server.application.coupon.dto.CouponResponse;
import kr.hhplus.be.server.swagger.CouponApiSpec;
import kr.hhplus.be.server.util.DummyDataUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class CouponController implements CouponApiSpec {

    private final DummyDataUtil dummyDataUtil;

    public CouponController(DummyDataUtil dummyDataUtil) {
        this.dummyDataUtil = dummyDataUtil;
    }

    @PostMapping("/coupon/issue")
    @Override
    public ResponseEntity<CouponResponse.Issue> issue(@RequestBody CouponRequest.Issue request){
        return ResponseEntity.ok(dummyDataUtil.getCouponIssue());
    }

    @GetMapping("/coupons/user/{userId}") // user_id -> userId
    @Override
    public ResponseEntity<CouponResponse.SelectByUserId> selectByUserId(@PathVariable long userId){ // user_id -> userId
        return ResponseEntity.ok(dummyDataUtil.getCouponSelectByUserId());
    }

    @GetMapping("/coupons/{status}")
    public ResponseEntity<CouponResponse.SelectByStatus> selectByStatus(@PathVariable String status){
        return ResponseEntity.ok(dummyDataUtil.getCouponSelectByStatus(status));
    }
}