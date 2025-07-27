package kr.hhplus.be.server.presentation.balance;

import kr.hhplus.be.server.application.balance.dto.BalanceRequest;
import kr.hhplus.be.server.application.balance.dto.BalanceResponse;
import kr.hhplus.be.server.swagger.BalanceApiSpec;
import kr.hhplus.be.server.util.DummyDataUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class BalanceController implements BalanceApiSpec {

    private final DummyDataUtil dummyDataUtil;

    public BalanceController(DummyDataUtil dummyDataUtil) {
        this.dummyDataUtil = dummyDataUtil;
    }

    @PatchMapping("/user/balance/charge")
    @Override
    public ResponseEntity<BalanceResponse> charge(@RequestBody BalanceRequest request){
        return ResponseEntity.ok(dummyDataUtil.getUserBalanceCharge());
    }

    @GetMapping("/user/balance/{userId}")
    @Override
    public ResponseEntity<BalanceResponse> selectBalanceByUserId(@PathVariable long userId){
        return ResponseEntity.ok(dummyDataUtil.getUserSelectBalanceByUserId());
    }
}
