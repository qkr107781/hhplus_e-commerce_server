package kr.hhplus.be.server.presentation.user;

import kr.hhplus.be.server.util.DummyDataUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class UserController {

    private final DummyDataUtil dummyDataUtil;

    public UserController(DummyDataUtil dummyDataUtil) {
        this.dummyDataUtil = dummyDataUtil;
    }

    @PatchMapping("/user/balance/charge")
    public ResponseEntity<UserResponse.Charge> charge(@RequestBody UserRequest.Charge request){
        return ResponseEntity.ok(UserResponse.Charge.from(dummyDataUtil.getUserBalanceCharge()));
    }

    @GetMapping("/user/balance/{user_id}")
    public ResponseEntity<UserResponse.SelectBalanceByUserId> selectBalanceByUserId(@PathVariable long user_id){
        return ResponseEntity.ok(UserResponse.SelectBalanceByUserId.from(dummyDataUtil.getUserSelectBalanceByUserId()));
    }
}
