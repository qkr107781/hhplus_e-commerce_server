package kr.hhplus.be.server.integration.application.product;

import kr.hhplus.be.server.ServerApplication;
import kr.hhplus.be.server.TestContainersConfiguration;
import kr.hhplus.be.server.application.product.dto.ProductResponse;
import kr.hhplus.be.server.application.product.facade.ProductFacadeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {ServerApplication.class, TestContainersConfiguration.class})
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // 테스트컨테이너에서 외부 DB 사용하도록 함
@Sql(scripts = "/productStatistics.sql",executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS) //테스트 실행 시 해당 .sql 파일내의 쿼리 실행 -> 테이블 생성 후 실행됨
@Sql(scripts = "/delete.sql",executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS) //이 클래스 테스트 종료 시 데이터 클랜징
public class ProductFacadeTest {

    @Autowired
    ProductFacadeService productFacadeService;

    @Test
    @Transactional
    @DisplayName("인기 상품 통계")
    void statistics(){
        //Given
        //사전 실행된 productStatistics.sql에서 balance=0으로 데이터 입력했음

        //When
        List<ProductResponse.Statistics> productStatisticsList = productFacadeService.selectTop5SalesProductBySpecificRange();

        //Then
        assertEquals(5,productStatisticsList.size());

        assertEquals("옵션5",productStatisticsList.get(0).productName());
        assertEquals(81L,productStatisticsList.get(0).salesQuantity());

        assertEquals("옵션9",productStatisticsList.get(1).productName());
        assertEquals(62L,productStatisticsList.get(1).salesQuantity());

        assertEquals("옵션4",productStatisticsList.get(2).productName());
        assertEquals(60L,productStatisticsList.get(2).salesQuantity());

        assertEquals("옵션6",productStatisticsList.get(3).productName());
        assertEquals(60L,productStatisticsList.get(3).salesQuantity());

        assertEquals("옵션8",productStatisticsList.get(4).productName());
        assertEquals(42L,productStatisticsList.get(4).salesQuantity());
    }

}
