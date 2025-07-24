package kr.hhplus.be.server.persistence.balance.mapper;

import kr.hhplus.be.server.domain.balance.Balance;
import kr.hhplus.be.server.persistence.balance.entity.BalanceJpaEntity;

public class BalanceMapper {

    public Balance toDomain(BalanceJpaEntity jpaEntity){
        return Balance.builder().balanceId(jpaEntity.getBalanceId())
                                .userId(jpaEntity.getUserId())
                                .balance(jpaEntity.getBalance())
                                .lastChargeDate(jpaEntity.getLastChargeDate())
                                .build();
    }

    public BalanceJpaEntity toEntity(Balance domain){
        return BalanceJpaEntity.builder()
                                .balanceId(domain.getBalanceId())
                                .userId(domain.getUserId())
                                .balance(domain.getBalance())
                                .lastChargeDate(domain.getLastChargeDate())
                                .build();
    }

}
