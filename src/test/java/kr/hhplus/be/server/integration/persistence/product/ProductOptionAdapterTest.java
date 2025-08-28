package kr.hhplus.be.server.integration.persistence.product;

import kr.hhplus.be.server.TestContainersConfiguration;
import kr.hhplus.be.server.domain.product.ProductOption;
import kr.hhplus.be.server.persistence.product.ProductOptionAdapter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@Sql(scripts = "/productOption.sql",executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS) //테스트 실행 시 해당 .sql 파일내의 쿼리 실행 -> 테이블 생성 후 실행됨
@ComponentScan(basePackageClasses = ProductOptionAdapter.class)//@Component 사용 중인 Adapter 클래스 읽어오기 위함
class ProductOptionAdapterTest extends TestContainersConfiguration {

    @Autowired
    ProductOptionAdapter productOptionAdapter;

    @Test
    @DisplayName("상품 옵션 ID로 상품 옵션 조회 - findByProductOptionId()")
    void findByProductOptionId(){
        System.out.println("findByProductOptionId - 쿼리");
        //Given
        //사전 실행된 productOption.sql에서 balance=0으로 데이터 입력했음
        //When
        ProductOption productOption = productOptionAdapter.selectProductOptionByProductOptionId(1L);
        //Then
        assertEquals("옵션1",productOption.getOptionName());
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
        assertEquals(11L,afterSave.getProductOptionId());
        assertEquals(5_000L,afterSave.getPrice());
    }

    @Test
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