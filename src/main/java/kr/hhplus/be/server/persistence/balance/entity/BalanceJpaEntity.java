package kr.hhplus.be.server.persistence.balance.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.balance.Balance;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "balance")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BalanceJpaEntity {

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
    public BalanceJpaEntity(Long balanceId, Long userId, Long balance, LocalDateTime lastChargeDate) {
        this.balanceId = balanceId;
        this.userId = userId;
        this.balance = balance;
        this.lastChargeDate = lastChargeDate;
    }

    /**
     * 영속성 객체 상태 변경을 위함
     * @param domain:Domain Layer Domain 객체
     */
    public void updateStatement(Balance domain){
        this.balance = domain.getBalance();
        this.lastChargeDate = domain.getLastChargeDate();
    }
}