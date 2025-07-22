package kr.hhplus.be.server.application.balance;

import kr.hhplus.be.server.application.balance.dto.BalanceRequest;
import kr.hhplus.be.server.application.balance.dto.BalanceResponse;

public interface BalanceUseCase {

    BalanceResponse charge(BalanceRequest balanceChargeRequest);

    BalanceResponse selectBalanceByUserId(long userId);

}
