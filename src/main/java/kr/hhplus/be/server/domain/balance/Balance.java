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

    public boolean validateChargeAmount(long chargeAmount, long balance){
        if(MIN_CHARGE_AMOUNT > chargeAmount){
            return false;
        }
        if(NAX_CHARGE_AMOUNT_PER < chargeAmount){
            return false;
        }
        if(OVER_BALANCE < (chargeAmount + balance)){
            return false;
        }
        return true;
    }
}