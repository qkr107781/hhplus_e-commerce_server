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
    private Long balance_id;

    @Column(name = "user_id", nullable = false)
    private Long user_id;

    @Column(name = "balance", nullable = false)
    private Long balance;

    @Column(name = "last_charge_date", nullable = false)
    private LocalDateTime last_charge_date;

    @Builder
    public Balance(Long balance_id, Long user_id, Long balance, LocalDateTime last_charge_date) {
        this.balance_id = balance_id;
        this.user_id = user_id;
        this.balance = balance;
        this.last_charge_date = last_charge_date;
    }

    private final long MIN_CHARGE_AMOUNT = 1L;
    private final long NAX_CHARGE_AMOUNT_PER = 100_000L;
    private final long OVER_BALANCE = 1_000_000L;

    public boolean validateChargeAmount(long charge_amount, long balance){
        if(MIN_CHARGE_AMOUNT > charge_amount){
            return false;
        }
        if(NAX_CHARGE_AMOUNT_PER < charge_amount){
            return false;
        }
        if(OVER_BALANCE < (charge_amount + balance)){
            return false;
        }
        return true;
    }
}