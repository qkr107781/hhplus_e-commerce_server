package kr.hhplus.be.server.integration.persistence.product;

import com.querydsl.core.Tuple;
import kr.hhplus.be.server.TestContainersConfiguration;
import kr.hhplus.be.server.application.order.dto.OrderProductSummary;
import kr.hhplus.be.server.domain.order.QOrderProduct;
import kr.hhplus.be.server.domain.product.QProductOption;
import kr.hhplus.be.server.persistence.product.ProductStatisticsAdapter;
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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // 테스트컨테이너에서 외부 DB 사용하도록 함
@ActiveProfiles("test") //application-test.yml 읽어오도록 함
@Sql(scripts = "/productStatistics.sql",executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS) //테스트 실행 시 해당 .sql 파일내의 쿼리 실행 -> 테이블 생성 후 실행됨
@ComponentScan(basePackageClasses = ProductStatisticsAdapter.class)//@Component 사용 중인 Adapter 클래스 읽어오기 위함
@ContextConfiguration(classes = TestContainersConfiguration.class)//Spring boot Context 로딩 전 TestContainerConfiguration 읽어오게 하기 위함
class ProductStatisticsAdapterTest {

    @Autowired
    ProductStatisticsAdapter productStatisticsAdapter;

    @Test
    @Transactional
    @DisplayName("지난3일 인기상품 TOP5 조회 - QureyDSL")
    void queryDsl() {
        System.out.println("queryDSL - 쿼리");
        //Given
        //사전 실행된 productStatistics.sql에서 balance=0으로 데이터 입력했음
        OrderProductSummary orderProductSummary1 = new OrderProductSummary(5L,81L);
        OrderProductSummary orderProductSummary2 = new OrderProductSummary(9L,62L);
        OrderProductSummary orderProductSummary3 = new OrderProductSummary(4L,60L);
        OrderProductSummary orderProductSummary4 = new OrderProductSummary(6L,60L);
        OrderProductSummary orderProductSummary5 = new OrderProductSummary(8L,42L);
        List<OrderProductSummary> orderProductSummaryList = new ArrayList<>();
        orderProductSummaryList.add(orderProductSummary1);
        orderProductSummaryList.add(orderProductSummary2);
        orderProductSummaryList.add(orderProductSummary3);
        orderProductSummaryList.add(orderProductSummary4);
        orderProductSummaryList.add(orderProductSummary5);

        //When
        List<Tuple> productStatisticsList = productStatisticsAdapter.selectTop5SalseProductByLast3Days(orderProductSummaryList);

        //Then
        QOrderProduct orderProduct = QOrderProduct.orderProduct;
        QProductOption productOption = QProductOption.productOption;

//        Thread.sleep(600000);
        assertEquals(5,productStatisticsList.size());

        assertEquals("옵션5",productStatisticsList.get(0).get(productOption.optionName));
        assertEquals(81L,productStatisticsList.get(0).get(orderProduct.productQuantity.sum()));

        assertEquals("옵션9",productStatisticsList.get(1).get(productOption.optionName));
        assertEquals(62L,productStatisticsList.get(1).get(orderProduct.productQuantity.sum()));

        assertEquals("옵션4",productStatisticsList.get(2).get(productOption.optionName));
        assertEquals(60L,productStatisticsList.get(2).get(orderProduct.productQuantity.sum()));

        assertEquals("옵션6",productStatisticsList.get(3).get(productOption.optionName));
        assertEquals(60L,productStatisticsList.get(3).get(orderProduct.productQuantity.sum()));

        assertEquals("옵션8",productStatisticsList.get(4).get(productOption.optionName));
        assertEquals(42L,productStatisticsList.get(4).get(orderProduct.productQuantity.sum()));

    }

}