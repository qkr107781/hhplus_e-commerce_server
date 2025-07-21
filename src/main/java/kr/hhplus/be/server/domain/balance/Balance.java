package kr.hhplus.be.server.domain.balance;

import java.time.LocalDateTime;

public class Balance {

    private Long balanceId;
    private Long userId;
    private Long balance;
    private LocalDateTime lastChargeDate;

    public Balance(Long balanceId, Long userId, Long balance, LocalDateTime lastChargeDate) {
        this.balanceId = balanceId;
        this.userId = userId;
        this.balance = balance;
        this.lastChargeDate = lastChargeDate;
    }

    public long getBalance(){
        return balance;
    }

    private final long MIN_CHARGE_AMOUNT = 1L;
    private final long NAX_CHARGE_AMOUNT_PER = 100_000L;
    private final long OVER_BALANCE = 1_000_000L;

    public void validateChargeAmount(long chargeAmount, long balance){
        if(MIN_CHARGE_AMOUNT > chargeAmount){
            throw new IllegalArgumentException("min charge illegal");
        }
        if(NAX_CHARGE_AMOUNT_PER < chargeAmount){
            throw new IllegalArgumentException("max charge illegal");
        }
        if(OVER_BALANCE < (chargeAmount + balance)) {
            throw new IllegalArgumentException("over charge illegal");
        }
    }

    public void charge(long chargeAmount){
        validateChargeAmount(chargeAmount, balance);
        this.balance += chargeAmount;
        this.lastChargeDate = LocalDateTime.now();
    }
}