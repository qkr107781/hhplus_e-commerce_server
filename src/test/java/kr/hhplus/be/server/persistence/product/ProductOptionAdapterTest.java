package kr.hhplus.be.server.persistence.product;

import kr.hhplus.be.server.TestContainersConfiguration;
import kr.hhplus.be.server.domain.product.ProductOption;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // 테스트컨테이너에서 외부 DB 사용하도록 함
@ActiveProfiles("test") //application-test.yml 읽어오도록 함
@Sql(scripts = "/productOption.sql",executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS) //테스트 실행 시 해당 .sql 파일내의 쿼리 실행 -> 테이블 생성 후 실행됨
@ComponentScan(basePackageClasses = ProductOptionAdapter.class)//@Component 사용 중인 Adapter 클래스 읽어오기 위함
@ContextConfiguration(classes = TestContainersConfiguration.class)//Spring boot Context 로딩 전 TestContainerConfiguration 읽어오게 하기 위함
class ProductOptionAdapterTest {

    @Autowired
    ProductOptionAdapter productOptionAdapter;

    @Test
    @Transactional
    @DisplayName("상품 옵션 ID로 상품 옵션 목록 조회 - findByProductOptionIdIn()")
    void findByProductOptionIdIn(){
        System.out.println("findByProductOptionIdIn - 쿼리");
        //Given
        //사전 실행된 productOption.sql에서 balance=0으로 데이터 입력했음
        //When
        List<Long> productOptionIds = List.of(1L,2L,3L);
        List<ProductOption> productOptionList = productOptionAdapter.selectProductOptionListByProductOptionIds(productOptionIds);
        //Then
        assertEquals(3,productOptionList.size());
    }

    @Test
    @Transactional
    @DisplayName("상품 옵션 저장 - save() - insert")
    void save(){
        System.out.println("save - 쿼리");
        //Given
        ProductOption productOption = ProductOption.builder()
                .salesYn("Y")
                .price(5_000L)
                .productId(2L)
                .regDate(LocalDateTime.now())
                .optionName("option")
                .stockQuantity(5L)
                .totalQuantity(10L)
                .build();
        //When
        ProductOption afterSave = productOptionAdapter.save(productOption);
        //Then
        assertEquals(4L,afterSave.getProductOptionId());
        assertEquals(5_000L,afterSave.getPrice());
    }

    @Test
    @Transactional
    @DisplayName("상품 옵션 ID, 상품 ID로 상품 옵션 조회 - findByProductIdAndProductOptionId()")
    void findByProductIdAndProductOptionId(){
        System.out.println("findByProductIdAndProductOptionId - 쿼리");
        //Given
        //사전 실행된 productOption.sql에서 balance=0으로 데이터 입력했음
        //When
        ProductOption productOption = productOptionAdapter.selectProductOptionByProductIdAndProductOptionId(1L,2L);
        //Then
        assertEquals(20000,productOption.getPrice());
        assertEquals("옵션2",productOption.getOptionName());
    }

    @Test
    @Transactional
    @DisplayName("상품 ID,판매여부로 상품 옵션 목록 조회 - findByProductIdAndSalesYn()")
    void findByProductIdAndSalesYn(){
        System.out.println("findByProductIdAndSalesYn - 쿼리");
        //Given
        //사전 실행된 productOption.sql에서 balance=0으로 데이터 입력했음
        //When
        List<ProductOption> productOptionList = productOptionAdapter.selectProductOptionByProductIdAndSalesYn(1L,"Y");
        //Then
        assertEquals(1,productOptionList.size());
    }

}