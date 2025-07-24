package kr.hhplus.be.server.persistence.balance;

import kr.hhplus.be.server.application.balance.repository.BalanceRepository;
import kr.hhplus.be.server.domain.balance.Balance;
import org.springframework.stereotype.Component;

@Component
public class BalanceAdapter implements BalanceRepository {

    private final BalanceJpaRepository balanceJpaRepository;

    public BalanceAdapter(BalanceJpaRepository balanceJpaRepository) {
        this.balanceJpaRepository = balanceJpaRepository;
    }

    @Override
    public Balance findByUserId(long userId) {
        return balanceJpaRepository.findByUserId(userId);
    }

    @Override
    public Balance saveBalance(Balance balance) {
        return balanceJpaRepository.save(balance);
    }
}
