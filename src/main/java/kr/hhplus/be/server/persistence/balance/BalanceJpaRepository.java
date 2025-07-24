package kr.hhplus.be.server.persistence.balance;

import kr.hhplus.be.server.domain.balance.Balance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BalanceJpaRepository extends JpaRepository<Balance,Long> {

    Balance findByUserId(long userId);

}
