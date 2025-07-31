package kr.hhplus.be.server.unit.domain.balance;

import kr.hhplus.be.server.domain.balance.Balance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BalanceTest {

    private Balance balance;

    @BeforeEach
    void setUp() {
        balance = new Balance(1L,1L,990_000L,LocalDateTime.now());
    }

    @Test
    @DisplayName("[잔액 조회]조회 성공(잔액: 990,000원)")
    void selectBalance(){
        //Given
        //@BeforeEach에서 진행
        long userId = 1L;
        //When
        long userBalance = balance.selectBalance(userId);
        //Then
        assertEquals(990_000L,userBalance);
    }

    @Test
    @DisplayName("[잔액 조회]조회 실패(타 유저 잔액 조회)")
    void selectBalanceFail(){
        //Given
        //@BeforeEach에서 진행
        long userId = 2L;
        //When
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                                            () -> balance.selectBalance(userId),"user compare fail");
        //Then
        assertTrue(thrown.getMessage().contains("user compare fail"));
    }

    @ParameterizedTest
    @ValueSource(longs = {1L,500L,9_999L,1_000L,})
    @DisplayName("[잔액 충전]충전 성공(잔액: 990_000L원, 충전: [1원, 500원, 9,999원, 1,000원]")
    void charge(long chargeAmount){
        //Given
        Balance localBalance = new Balance(1L,1L,0L,LocalDateTime.now());
        //When
        localBalance.charge(chargeAmount);
        //Then
        assertEquals(chargeAmount,localBalance.getBalance());
    }

    @ParameterizedTest
    @ValueSource(longs = {-100L,0L})
    @DisplayName("[잔액 충전][최소 충전 금액 미달]입력받은 금액이 0 이하 일때 충전 실패(-100원,0원)")
    void minUseChargeAmount(long chargeAmount) {
        //Given
        //@BeforeEach에서 진행
        //When
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                                            () -> balance.validateChargeAmount(chargeAmount, balance.getBalance()),"min charge illegal");
        //Then
        assertTrue(thrown.getMessage().contains("min charge illegal"));
    }

    @ParameterizedTest
    @ValueSource(longs = {100_001L,110_000L})
    @DisplayName("[잔액 충전][1회 최대 충전 금액 초과]입력받은 금액이 100,000 초과 일때 충전 실패(100,001원, 110,000원)")
    void overChargeAmountPerCharge(long chargeAmount) {
        //Given
        //@BeforeEach에서 진행
        //When
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                                            () -> balance.validateChargeAmount(chargeAmount, balance.getBalance()),"max charge illegal");
        //Then
        assertTrue(thrown.getMessage().contains("max charge illegal"));
    }

    @ParameterizedTest
    @ValueSource(longs = {10_001L,11_000L})
    @DisplayName("[잔액 충전][최대 잔고 초과 충전 요청]최대 잔고 초과 되도록 충전 요청 시 해당 요청 충전 실패(잔액: 990,000원, 충전: [10,001원,11,000원])")
    void overMaxBalance(long chargeAmount) {
        //Given
        //@BeforeEach에서 진행
        //When
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                                            () -> balance.validateChargeAmount(chargeAmount, balance.getBalance()),"over charge illegal");
        //Then
        assertTrue(thrown.getMessage().contains("over charge illegal"));
    }

    @ParameterizedTest
    @ValueSource(longs = {990_000L,90_000L})
    @DisplayName("[잔액 차감]차감 금액 만큼 잔액에서 차감")
    void useBalance(long useAmount) throws Exception {
        //Given
        long ownBalance = 990_000L;
        Balance balance = new Balance(1L,1L,ownBalance,LocalDateTime.now());
        //When
        balance.useBalance(useAmount);
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
        Exception thrown = assertThrows(Exception.class,
                () -> balance.useBalance(useAmount),"not enough balance");
        //Then
        assertTrue(thrown.getMessage().contains("not enough balance"));
    }
}