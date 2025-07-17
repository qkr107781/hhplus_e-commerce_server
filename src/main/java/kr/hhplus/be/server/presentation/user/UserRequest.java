package kr.hhplus.be.server.presentation.user;

public class UserRequest {
    public record Charge(
        long user_id,
        long charge_amount
    ){
    }
}
