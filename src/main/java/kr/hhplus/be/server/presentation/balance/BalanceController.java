package kr.hhplus.be.server.presentation.balance;

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
    public ResponseEntity<BalanceResponse.Charge> charge(@RequestBody BalanceRequest.Charge request){
        return ResponseEntity.ok(BalanceResponse.Charge.from(dummyDataUtil.getUserBalanceCharge()));
    }

    @GetMapping("/user/balance/{user_id}")
    @Override
    public ResponseEntity<BalanceResponse.SelectBalanceByUserId> selectBalanceByUserId(@PathVariable long user_id){
        return ResponseEntity.ok(BalanceResponse.SelectBalanceByUserId.from(dummyDataUtil.getUserSelectBalanceByUserId()));
    }
}
