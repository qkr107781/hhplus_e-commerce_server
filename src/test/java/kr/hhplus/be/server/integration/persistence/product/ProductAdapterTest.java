package kr.hhplus.be.server.integration.persistence.product;

import kr.hhplus.be.server.TestContainersConfiguration;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.persistence.product.ProductAdapter;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // 테스트컨테이너에서 외부 DB 사용하도록 함
@ActiveProfiles("test") //application-test.yml 읽어오도록 함
@Sql(scripts = "/product.sql",executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS) //테스트 실행 시 해당 .sql 파일내의 쿼리 실행 -> 테이블 생성 후 실행됨
@ComponentScan(basePackageClasses = ProductAdapter.class)//@Component 사용 중인 Adapter 클래스 읽어오기 위함
@ContextConfiguration(classes = TestContainersConfiguration.class)//Spring boot Context 로딩 전 TestContainerConfiguration 읽어오게 하기 위함
class ProductAdapterTest {

    @Autowired
    ProductAdapter productAdapter;

    @Test
    @Transactional
    @DisplayName("상품 전체 조회 - findAll()")
    void findAll(){
        System.out.println("findAll - 쿼리");
        //Given
        //사전 실행된 product.sql에서 balance=0으로 데이터 입력했음
        //When
        List<Product> productList = productAdapter.selectAllProduct();
        //Then
        assertEquals(2,productList.size());
    }

    @Test
    @Transactional
    @DisplayName("상품 ID로 조회 - findByProductId()")
    void findByProductId(){
        System.out.println("findByProductId - 쿼리");
        //Given
        //사전 실행된 product.sql에서 balance=0으로 데이터 입력했음
        //When
        Product product = productAdapter.selectProductByProductId(1L);
        //Then
        assertEquals("티셔츠1L",product.getName());
    }
}