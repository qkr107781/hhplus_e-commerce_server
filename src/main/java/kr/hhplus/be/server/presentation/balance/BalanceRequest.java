package kr.hhplus.be.server.presentation.balance;

public class BalanceRequest {
    public record Charge(
        long user_id,
        long charge_amount
    ){
    }
}
