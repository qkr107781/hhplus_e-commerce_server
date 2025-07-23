package kr.hhplus.be.server.application.balance.service;

import kr.hhplus.be.server.application.balance.dto.BalanceRequest;
import kr.hhplus.be.server.application.balance.dto.BalanceResponse;
import kr.hhplus.be.server.application.balance.repository.BalanceRepository;
import kr.hhplus.be.server.domain.balance.Balance;
import org.springframework.stereotype.Service;

@Service
public class BalanceService implements BalanceUseCase{

    private final BalanceRepository balanceRepository;

    public BalanceService(BalanceRepository balanceRepository) {
        this.balanceRepository = balanceRepository;
    }

    /**
     * 사용자 잔액 조회
     * @param userId: 사용자 ID
     * @return 사용자 ID, 보유 잔액, 마지막 충전일
     */
    @Override
    public BalanceResponse selectBalanceByUserId(long userId) {
        Balance userBalance = balanceRepository.findByUserId(userId);
        return BalanceResponse.from(userBalance);
    }

    /**
     * 잔액 충전
     * @param balanceRequest: 사용자 ID, 충전 요청 금액
     * @return 사용자 ID, 충전 후 잔액, 마지막 충전일
     */
    @Override
    public BalanceResponse charge(BalanceRequest balanceRequest) {
        Balance userBalance = balanceRepository.findByUserId(balanceRequest.userId());
        
        //충전 금액 유효성 검증
        userBalance.validateChargeAmount(balanceRequest.chargeAmount(), userBalance.getBalance());
        //충전
        userBalance.charge(balanceRequest.chargeAmount());

        //충전 금액 insert
        Balance chargeBalance = balanceRepository.save(userBalance);
        return BalanceResponse.from(chargeBalance);
    }

}
