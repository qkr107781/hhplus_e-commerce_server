package kr.hhplus.be.server.domain.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    @Test
    @DisplayName("[상품 조회]조회 성공")
    void selectProduct(){
        //Given
        long productId = 1L;
        String name = "티셔츠";
        String description = "티셔츠 설명";

        ProductOption productOption1 = new ProductOption(1L,1L,"XL",20_000L,30L,15L,"Y", LocalDateTime.now());
        ProductOption productOption2 = new ProductOption(2L,1L,"L",20_000L,20L,5L,"Y", LocalDateTime.now());
        ProductOption productOption3 = new ProductOption(3L,1L,"M",20_000L,10L,2L,"Y", LocalDateTime.now());

        List<ProductOption> products = new ArrayList<>();
        products.add(productOption1);
        products.add(productOption2);
        products.add(productOption3);

        //When
        Product product = new Product(productId,name,description,products);

        //Then
        assertEquals(productId,product.getProductId());
        assertEquals(name,product.getName());
        assertEquals(description,product.getDescription());
        assertEquals(products,product.getProductOptions());
    }

    @Test
    @DisplayName("[상품 조회]입력받은 상품 ID에 해당하는 상품 조회 불가(미존재 or 존재하나 판매중이 아닌 상품)")
    void selectProductFail(){
        //Given
        long productId = 1L;
        String name = "티셔츠";
        String description = "티셔츠 설명";

        ProductOption productOption1 = new ProductOption(1L,1L,"XL",20_000L,30L,15L,"Y", LocalDateTime.now());
        ProductOption productOption2 = new ProductOption(2L,1L,"L",20_000L,20L,5L,"Y", LocalDateTime.now());
        ProductOption productOption3 = new ProductOption(3L,1L,"M",20_000L,10L,2L,"N", LocalDateTime.now());

        List<ProductOption> products = new ArrayList<>();
        products.add(productOption1);
        products.add(productOption2);
        products.add(productOption3);

        //When
        Product product = new Product(productId,name,description,products);

        //Then
        assertEquals(productId,product.getProductId());
        assertEquals(name,product.getName());
        assertEquals(description,product.getDescription());
        assertEquals(2,product.getProductOptions().size());
    }

}