package kr.hhplus.be.server.presentation.user;

public class UserResponse {
    public record Charge(
        long user_id,
        long charge_amount,
        long balance
    ){
        public static UserResponse.Charge from(UserResponse.Charge charge){
            return new UserResponse.Charge(charge.user_id,charge.charge_amount,charge.balance);
        }
    }

    public record SelectBalanceByUserId(
            long user_id,
            long balance
    ){
        public static UserResponse.SelectBalanceByUserId from(UserResponse.SelectBalanceByUserId charge){
            return new UserResponse.SelectBalanceByUserId(charge.user_id,charge.balance);
        }
    }
}
