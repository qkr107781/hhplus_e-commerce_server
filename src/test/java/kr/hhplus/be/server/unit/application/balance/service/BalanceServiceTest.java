package kr.hhplus.be.server.unit.application.balance.service;

import kr.hhplus.be.server.application.balance.dto.BalanceRequest;
import kr.hhplus.be.server.application.balance.dto.BalanceResponse;
import kr.hhplus.be.server.application.balance.repository.BalanceRepository;
import kr.hhplus.be.server.application.balance.service.BalanceService;
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
        Balance balance = Balance.builder()
                                .balanceId(1L)
                                .userId(userId)
                                .balance(userBalance)
                                .lastChargeDate(LocalDateTime.now())
                                .build();

        //충전 후 잔액
        Balance afterChargeBalance = Balance.builder()
                                            .balanceId(1L)
                                            .userId(userId)
                                            .balance(userBalance+chargeAmount)
                                            .lastChargeDate(LocalDateTime.now())
                                            .build();

        when(balanceRepository.findByUserId(userId)).thenReturn(balance);
        when(balanceRepository.save(any(Balance.class))).thenReturn(afterChargeBalance);

        //When
        BalanceRequest request = new BalanceRequest(userId,chargeAmount);
        BalanceService balanceService = new BalanceService(balanceRepository);
        BalanceResponse response =  balanceService.charge(request);

        //Then
        assertEquals(userBalance+chargeAmount,response.balance());
    }

    @ParameterizedTest
    @ValueSource(longs = {990_000L,90_000L})
    @DisplayName("[잔액 차감]차감 금액 만큼 잔액에서 차감")
    void useBalance(long useAmount) throws Exception {
        //Given
        long ownBalance = 990_000L;
        Balance balance = new Balance(1L,1L,ownBalance,LocalDateTime.now());

        //When
        BalanceService balanceService = new BalanceService(balanceRepository);
        balanceService.useBalance(balance,useAmount);

        //Then
        assertEquals(ownBalance-useAmount,balance.getBalance());
    }

    @ParameterizedTest
    @ValueSource(longs = {990_001L,999_000L})
    @DisplayName("[잔액 차감]잔액 이상으로 금액 차감 요청 시 실패처리")
    void overUse(long useAmount){
        //Given
        long ownBalance = 990_000L;
        Balance balance = new Balance(1L,1L,ownBalance,LocalDateTime.now());

        //When
        BalanceService balanceService = new BalanceService(balanceRepository);
        Exception thrown = assertThrows(Exception.class,
                () -> balanceService.useBalance(balance,useAmount),"not enough balance");

        //Then
        assertTrue(thrown.getMessage().contains("not enough balance"));
    }

}