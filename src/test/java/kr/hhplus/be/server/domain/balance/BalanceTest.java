package kr.hhplus.be.server.domain.balance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BalanceTest {

    private Balance balance;

    @BeforeEach
    void setUp() {
        balance = Balance.builder()
                    .user_id(1L)
                    .balance(990_000L)
                    .last_charge_date(LocalDateTime.now())
                    .build();
    }

    @ParameterizedTest
    @ValueSource(longs = {0L,100_001L})
    @DisplayName("[잔액 충전][최소 충전 금액 미달]입력받은 금액이 0 이하 일때 충전 실패(-100원,0원)")
    void minUseChargeAmount(long charge_amount) {
        //Given
        //@BeforeEach에서 진행
        //When
        boolean result = balance.validateChargeAmount(charge_amount,balance.getBalance());
        //Then
        assertFalse(result);
    }

    @ParameterizedTest
    @ValueSource(longs = {100_001L,110_000L})
    @DisplayName("[잔액 충전][1회 최대 충전 금액 초과]입력받은 금액이 100,000 초과 일때 충전 실패(100,001원, 110,000원)")
    void overChargeAmountPerCharge(long charge_amount) {
        //Given
        //@BeforeEach에서 진행
        //When
        boolean result = balance.validateChargeAmount(charge_amount,balance.getBalance());
        //Then
        assertFalse(result);
    }

    @ParameterizedTest
    @ValueSource(longs = {10_001L,11_000L})
    @DisplayName("[잔액 충전][최대 잔고 초과 충전 요청]최대 잔고 초과 되도록 충전 요청 시 해당 요청 충전 실패(잔액: 990,000원, 충전: [10,001원,11,000원])")
    void overMaxBalance(long charge_amount) {
        //Given
        //@BeforeEach에서 진행
        //When
        boolean result = balance.validateChargeAmount(charge_amount,balance.getBalance());
        //Then
        assertFalse(result);
    }
}