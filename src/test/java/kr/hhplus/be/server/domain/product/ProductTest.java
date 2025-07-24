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

        Product product = Product.builder()
                .productId(productId)
                .name(name)
                .description(description)
                .build();

        ProductOption productOption1 = new ProductOption(1L,product,"XL",20_000L,30L,15L,"Y", LocalDateTime.now());
        ProductOption productOption2 = new ProductOption(2L,product,"L",20_000L,20L,5L,"Y", LocalDateTime.now());
        ProductOption productOption3 = new ProductOption(3L,product,"M",20_000L,10L,2L,"Y", LocalDateTime.now());

        List<ProductOption> products = new ArrayList<>();
        products.add(productOption1);
        products.add(productOption2);
        products.add(productOption3);

        product.addProductOptionList(products);

        //When
        Product resultpProduct = Product.builder()
                            .productId(productId)
                            .name(name)
                            .description(description)
                            .build();

        //Then
        assertEquals(productId,product.getProductId());
        assertEquals(name,product.getName());
        assertEquals(description,product.getDescription());
        assertEquals(products,product.getProductOptions());
    }

}