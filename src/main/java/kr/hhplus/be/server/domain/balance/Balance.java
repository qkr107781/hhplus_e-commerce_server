package kr.hhplus.be.server.domain.balance;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class Balance {

    private Long balanceId;
    private Long userId;
    private Long balance;
    private LocalDateTime lastChargeDate;

    private final long MIN_CHARGE_AMOUNT = 1L;
    private final long NAX_CHARGE_AMOUNT_PER = 100_000L;
    private final long OVER_BALANCE = 1_000_000L;

    /**
     * 충전 금액 유효성 검증
     * @param chargeAmount:충전 금액
     * @param balance: 현재 잔액
     */
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

    /**
     * 잔액 충전: 잔액 충전 전 충천 금액 유효성 검증 진행 후 기존 잔액에 충전 금액 더하여 저장
     * @param chargeAmount:충전 금액
     */
    public void charge(long chargeAmount){
        validateChargeAmount(chargeAmount, this.balance);
        this.balance += chargeAmount;
        this.lastChargeDate = LocalDateTime.now();
    }

    /**
     * 사용자 잔액 조회
     * @param userId:사용자ID
     * @return 현재 잔액
     */
    public long selectBalance(long userId){
        if(this.userId != userId){
            throw new IllegalArgumentException("user compare fail");
        }
        return this.balance;
    }
}