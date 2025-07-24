package kr.hhplus.be.server.domain.balance;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "balance")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Balance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "balance_id")
    private Long balanceId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "balance", nullable = false)
    private Long balance;

    @Column(name = "last_charge_date", nullable = false)
    private LocalDateTime lastChargeDate;

    @Builder
    public Balance(Long balanceId, Long userId, Long balance, LocalDateTime lastChargeDate) {
        this.balanceId = balanceId;
        this.userId = userId;
        this.balance = balance;
        this.lastChargeDate = lastChargeDate;
    }

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

    /**
     * 잔액 차감
     * @param useAmount: 차감 금액
     * @throws Exception
     */
    public void useBalance(long useAmount) throws Exception {
        if(this.balance - useAmount < 0){
            throw new Exception("not enough balance");
        }
        this.balance -= useAmount;
    }
}