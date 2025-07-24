package kr.hhplus.be.server.persistence.balance.repository;

import kr.hhplus.be.server.application.balance.repository.BalanceRepository;
import kr.hhplus.be.server.domain.balance.Balance;
import kr.hhplus.be.server.persistence.balance.entity.BalanceJpaEntity;
import kr.hhplus.be.server.persistence.balance.mapper.BalanceMapper;
import org.springframework.stereotype.Component;

@Component
public class BalanceAdapter implements BalanceRepository {

    private final BalanceJpaRepository balanceJpaRepository;
    private final BalanceMapper balanceMapper;

    public BalanceAdapter(BalanceJpaRepository balanceJpaRepository,BalanceMapper balanceMapper) {
        this.balanceJpaRepository = balanceJpaRepository;
        this.balanceMapper = balanceMapper;
    }

    @Override
    public Balance findByUserId(long userId) {
        return balanceMapper.toDomain(balanceJpaRepository.findByUserId(userId));
    }

    @Override
    public Balance saveBalance(Balance balance) {
        BalanceJpaEntity balanceJpaEntity = balanceJpaRepository.findByUserId(balance.getUserId());

        balanceJpaEntity.updateStatement(balance);

        return balanceMapper.toDomain(balanceJpaEntity);
    }
}
