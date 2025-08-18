package kr.hhplus.be.server.integration.persistence.balance;

import kr.hhplus.be.server.ServerApplication;
import kr.hhplus.be.server.TestContainersConfiguration;
import kr.hhplus.be.server.domain.balance.Balance;
import kr.hhplus.be.server.persistence.balance.BalanceAdapter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {ServerApplication.class, TestContainersConfiguration.class})
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // 테스트컨테이너에서 외부 DB 사용하도록 함
@Sql(scripts = "/balance.sql",executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS) //테스트 실행 시 해당 .sql 파일내의 쿼리 실행 -> 테이블 생성 후 실행됨
@Sql(scripts = "/delete.sql",executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS) //이 클래스 테스트 종료 시 데이터 클랜징
@ComponentScan(basePackageClasses = BalanceAdapter.class)//@Component 사용 중인 Adapter 클래스 읽어오기 위함
class BalanceAdapterTest {

    @Autowired
    BalanceAdapter balanceAdapter;

    @Test
    @Transactional
//    @Commit //update 쿼리 확인용 -> 단독으로 실행해야됨! 아니면 다음 테스트에 영향끼침
    @DisplayName("잔액 충전 - save() - update")
    void save(){
        System.out.println("save update 쿼리");
        //Given
        Balance balance = balanceAdapter.findByUserId(1L);
        //When
        balance.charge(30_000L);
        balance = balanceAdapter.save(balance);
        //Then
        assertEquals(130_000L,balance.getBalance());
    }

    @Test
    @Transactional
    void findByUserId(){
        System.out.println("findByUserId 쿼리");
        //Given
        //사전 실행된 balance.sql에서 balance=0으로 데이터 입력했음
        //When
        Balance balance = balanceAdapter.findByUserId(1L);
        //Then
        assertEquals(100_000L,balance.getBalance());
    }
}