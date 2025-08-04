package kr.hhplus.be.server.unit.domain.product;

import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductOption;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ProductOptionTest {

    @Test
    @DisplayName("[상품 주문][재고 부족]재고 0개 일때 차감 요청 시 실패 처리")
    void outOfStock(){
        //Given
        Product product = Product.builder()
                .productId(1L)
                .name("티셔츠")
                .build();
        // 상품 옵션 세팅 (재고 차감 전의 초기 상태)
        ProductOption productOption = ProductOption.builder()
                .productOptionId(1L)
                .productId(product.getProductId())
                .optionName("XL")
                .price(20_000L)
                .salesYn("Y")
                .totalQuantity(30L)
                .stockQuantity(0L)
                .regDate(LocalDateTime.now())
                .build();

        //When
        Exception thrown = assertThrows(Exception.class,
                productOption::decreaseProductQuantity,"stock empty");
        //Then
        assertTrue(thrown.getMessage().contains("stock empty"));
    }

    @Test
    @DisplayName("[상품 주문][재고 차감]재고 차감 성공")
    void decreaseStock() throws Exception {
        //Given
        Product product = Product.builder()
                .productId(1L)
                .name("티셔츠")
                .build();
        // 상품 옵션 세팅 (재고 차감 전의 초기 상태)
        ProductOption productOption = ProductOption.builder()
                .productOptionId(1L)
                .productId(product.getProductId())
                .optionName("XL")
                .price(20_000L)
                .salesYn("Y")
                .totalQuantity(30L)
                .stockQuantity(20L)
                .regDate(LocalDateTime.now())
                .build();

        //When
        productOption.decreaseProductQuantity();

        //Then
        assertEquals(19L,productOption.getStockQuantity());
    }

    @Test
    @DisplayName("[상품 주문][재고 복구]재고 복구 성공")
    void restoreStock() throws Exception {
        //Given
        Product product = Product.builder()
                .productId(1L)
                .name("티셔츠")
                .build();
        // 상품 옵션 세팅 (재고 차감 전의 초기 상태)
        ProductOption productOption = ProductOption.builder()
                .productOptionId(1L)
                .productId(product.getProductId())
                .optionName("XL")
                .price(20_000L)
                .salesYn("Y")
                .totalQuantity(30L)
                .stockQuantity(20L)
                .regDate(LocalDateTime.now())
                .build();

        //When
        productOption.decreaseProductQuantity();
        productOption.decreaseProductQuantity();
        productOption.decreaseProductQuantity();
        productOption.decreaseProductQuantity();
        productOption.decreaseProductQuantity();

        //Then
        assertEquals(15L,productOption.getStockQuantity());

        //When
        productOption.restoreProductQuantity(5L);

        //Then
        assertEquals(20L,productOption.getStockQuantity());
    }
}