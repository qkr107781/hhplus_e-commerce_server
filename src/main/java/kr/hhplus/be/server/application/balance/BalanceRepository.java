package kr.hhplus.be.server.application.balance;

import kr.hhplus.be.server.domain.balance.Balance;

public interface BalanceRepository {

    Balance findByUserId(long userId);

    Balance save(Balance balance);

}
