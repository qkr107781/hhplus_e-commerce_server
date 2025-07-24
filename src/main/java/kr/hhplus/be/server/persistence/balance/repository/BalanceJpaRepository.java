package kr.hhplus.be.server.persistence.balance.repository;

import kr.hhplus.be.server.domain.balance.Balance;
import kr.hhplus.be.server.persistence.balance.entity.BalanceJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BalanceJpaRepository extends JpaRepository<Balance,Long> {

    BalanceJpaEntity findByUserId(long userId);

}
