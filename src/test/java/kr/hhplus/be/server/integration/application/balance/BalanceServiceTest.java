package kr.hhplus.be.server.integration.application.balance;

import kr.hhplus.be.server.TestContainersConfiguration;
import kr.hhplus.be.server.application.balance.dto.BalanceRequest;
import kr.hhplus.be.server.application.balance.dto.BalanceResponse;
import kr.hhplus.be.server.application.balance.service.BalanceService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.*;

@Sql(scripts = "/balance.sql",executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS) //테스트 실행 시 해당 .sql 파일내의 쿼리 실행 -> 테이블 생성 후 실행됨
public class BalanceServiceTest extends TestContainersConfiguration {

    @Autowired
    BalanceService balanceService;

    @Test
    @DisplayName("잔액 충전")
    void charge(){
        //Given
        BalanceRequest balanceRequest = new BalanceRequest(1L,30_000L);

        //When
        BalanceResponse balanceResponse = balanceService.charge(balanceRequest);

        //Then
        assertEquals(130_000L,balanceResponse.balance());
    }

}
