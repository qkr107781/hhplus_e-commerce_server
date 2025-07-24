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
        ProductOption productOption = new ProductOption(1L,1L,"XL",20_000L,30L,15L,"Y", LocalDateTime.now());

        //When
        Exception thrown = assertThrows(Exception.class,
                productOption::decreaseProductQuantity,"stock empty");
        //Then
        assertTrue(thrown.getMessage().contains("stock empty"));
    }

    @Test
    @DisplayName("[상품 주문][재고 차감]재고 차감 성공")
    void canIssueCoupon() throws Exception {
        //Given
        ProductOption productOption = new ProductOption(1L,1L,"XL",20_000L,30L,15L,"Y", LocalDateTime.now());

        //When
        productOption.decreaseProductQuantity();

        //Then
        assertEquals(14L,productOption.getStockQuantity());
    }
}