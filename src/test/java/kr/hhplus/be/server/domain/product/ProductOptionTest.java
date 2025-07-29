package kr.hhplus.be.server.domain.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ProductOptionTest {

    @Test
    @DisplayName("[상품 주문][재고 부족]재고 0개 일때 차감 요청 시 실패 처리")
    void notIssuingStatus(){
        //Given
        Product product = Product.builder()
                .productId(1L)
                .name("티셔츠")
                .build();
        // 상품 옵션 세팅 (재고 차감 전의 초기 상태)
        ProductOption productOption = ProductOption.builder()
                .productOptionId(1L)
                .product(product)
                .optionName("XL")
                .price(20_000L)
                .salesYn("Y")
                .regDate(LocalDateTime.now())
                .build();

        // 상품 재고
        ProductStock productStock = ProductStock.builder()
                .productStockId(1L)
                .totalQuantity(30L)
                .stockQuantity(0L)
                .productOption(productOption)
                .build();


        productOption.addProductStock(productStock);

        //When
        Exception thrown = assertThrows(Exception.class,
                productOption.getProductStock()::decreaseProductQuantity,"stock empty");
        //Then
        assertTrue(thrown.getMessage().contains("stock empty"));
    }

    @Test
    @DisplayName("[상품 주문][재고 차감]재고 차감 성공")
    void canIssueCoupon() throws Exception {
        //Given
        Product product = Product.builder()
                .productId(1L)
                .name("티셔츠")
                .build();
        // 상품 옵션 세팅 (재고 차감 전의 초기 상태)
        ProductOption productOption = ProductOption.builder()
                .productOptionId(1L)
                .product(product)
                .optionName("XL")
                .price(20_000L)
                .salesYn("Y")
                .regDate(LocalDateTime.now())
                .build();

        // 상품 재고
        ProductStock productStock = ProductStock.builder()
                .productStockId(1L)
                .totalQuantity(30L)
                .stockQuantity(20L)
                .productOption(productOption)
                .build();


        productOption.addProductStock(productStock);

        //When
        productOption.getProductStock().decreaseProductQuantity();

        //Then
        assertEquals(19L,productOption.getProductStock().getStockQuantity());
    }
}