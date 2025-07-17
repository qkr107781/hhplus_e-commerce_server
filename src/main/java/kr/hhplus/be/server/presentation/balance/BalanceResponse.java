package kr.hhplus.be.server.presentation.balance;

public class BalanceResponse {
    public record Charge(
        long user_id,
        long charge_amount,
        long balance
    ){
        public static BalanceResponse.Charge from(BalanceResponse.Charge charge){
            return new BalanceResponse.Charge(charge.user_id,charge.charge_amount,charge.balance);
        }
    }

    public record SelectBalanceByUserId(
            long user_id,
            long balance
    ){
        public static BalanceResponse.SelectBalanceByUserId from(BalanceResponse.SelectBalanceByUserId charge){
            return new BalanceResponse.SelectBalanceByUserId(charge.user_id,charge.balance);
        }
    }
}
