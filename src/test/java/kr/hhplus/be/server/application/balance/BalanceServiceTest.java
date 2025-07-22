package kr.hhplus.be.server.application.balance;

import kr.hhplus.be.server.application.balance.dto.BalanceRequest;
import kr.hhplus.be.server.application.balance.dto.BalanceResponse;
import kr.hhplus.be.server.domain.balance.Balance;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class BalanceServiceTest {

    @Mock
    BalanceRepository balanceRepository;

    @ParameterizedTest
    @ValueSource(longs = {1L,500L,9_999L,1_000L,})
    @DisplayName("[잔액 충전]충전 성공(잔액: 990_000L원, 충전: [1원, 500원, 9,999원, 1,000원]")
    void charge(long chargeAmount){
        //Given
        //충전 전 잔액
        long userId = 1L;
        long userBalance = 50_000L;
        Balance balance = new Balance(1L,userId,userBalance,LocalDateTime.now());

        //충전 후 잔액
        Balance afterChargeBalance = new Balance(1L,userId,userBalance+chargeAmount,LocalDateTime.now());

        when(balanceRepository.findByUserId(userId)).thenReturn(balance);
        when(balanceRepository.save(any(Balance.class))).thenReturn(afterChargeBalance);

        //When
        BalanceRequest request = new BalanceRequest(userId,chargeAmount);
        BalanceService balanceService = new BalanceService(balanceRepository);
        BalanceResponse response =  balanceService.charge(request);

        //Then
        assertEquals(userBalance+chargeAmount,response.balance());
    }

}