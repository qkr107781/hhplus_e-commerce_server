package kr.hhplus.be.server.application.balance.repository;

import kr.hhplus.be.server.domain.balance.Balance;

public interface BalanceRepository {

    Balance findByUserId(long userId);

    Balance save(Balance balance);

}
