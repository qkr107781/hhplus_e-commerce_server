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
                .build();

        // 상품 옵션 세팅 (재고 차감 전의 초기 상태)
        ProductOption productOption1 = ProductOption.builder()
                .productOptionId(1L)
                .product(product)
                .optionName("XL")
                .price(20_000L)
                .salesYn("Y")
                .regDate(LocalDateTime.now())
                .build();
        ProductOption productOption2 = ProductOption.builder()
                .productOptionId(2L)
                .product(product)
                .optionName("L")
                .price(20_000L)
                .salesYn("Y")
                .regDate(LocalDateTime.now())
                .build();
        ProductOption productOption3 = ProductOption.builder()
                .productOptionId(3L)
                .product(product)
                .optionName("M")
                .price(20_000L)
                .salesYn("Y")
                .regDate(LocalDateTime.now())
                .build();

        // 상품 재고
        ProductStock productStock1 = ProductStock.builder()
                .productStockId(1L)
                .totalQuantity(30L)
                .stockQuantity(20L)
                .productOption(productOption1)
                .build();

        ProductStock productStock2 = ProductStock.builder()
                .productStockId(2L)
                .totalQuantity(30L)
                .stockQuantity(20L)
                .productOption(productOption2)
                .build();

        ProductStock productStock3 = ProductStock.builder()
                .productStockId(3L)
                .totalQuantity(30L)
                .stockQuantity(20L)
                .productOption(productOption3)
                .build();

        productOption1.addProductStock(productStock1);
        productOption2.addProductStock(productStock2);
        productOption3.addProductStock(productStock3);

        List<ProductOption> products = new ArrayList<>();
        products.add(productOption1);
        products.add(productOption2);
        products.add(productOption3);

        product.addProductOptionList(products);

        //When
        Product resultpProduct = Product.builder()
                            .productId(productId)
                            .name(name)
                            .build();

        //Then
        assertEquals(productId,product.getProductId());
        assertEquals(name,product.getName());
        assertEquals(products,product.getProductOptions());
    }

}