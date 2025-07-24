package kr.hhplus.be.server.application.product.service;

import kr.hhplus.be.server.application.product.dto.ProductResponse;
import kr.hhplus.be.server.application.product.repository.ProductRepository;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductOption;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    ProductRepository productRepository;

    @Test
    @DisplayName("[상품] 판매중인 상품 조회")
    void selectSalesProductList(){
        //Given
        long productId = 1L;
        String name = "티셔츠";
        String description = "티셔츠 설명";

        long productId2 = 2L;
        String name2 = "티셔츠";
        String description2 = "티셔츠 설명";

        Product product = Product.builder()
                .productId(productId)
                .name(name)
                .description(description)
                .build();

        Product product2 = Product.builder()
                .productId(productId2)
                .name(name2)
                .description(description2)
                .build();

        ProductOption productOption1 = new ProductOption(1L,product,"XL",20_000L,30L,15L,"Y", LocalDateTime.now());
        ProductOption productOption2 = new ProductOption(2L,product,"L",20_000L,20L,5L,"Y", LocalDateTime.now());
        ProductOption productOption3 = new ProductOption(3L,product,"M",20_000L,10L,2L,"Y", LocalDateTime.now());

        List<ProductOption> products = new ArrayList<>();
        products.add(productOption1);
        products.add(productOption2);
        products.add(productOption3);

        product.addProductOptionList(products);

        ProductOption productOption11 = new ProductOption(4L,product2,"XL",20_000L,30L,15L,"Y", LocalDateTime.now());
        ProductOption productOption22 = new ProductOption(5L,product2,"L",20_000L,20L,5L,"Y", LocalDateTime.now());
        ProductOption productOption33 = new ProductOption(6L,product2,"M",20_000L,10L,2L,"Y", LocalDateTime.now());

        List<ProductOption> products2 = new ArrayList<>();
        products2.add(productOption11);
        products2.add(productOption22);
        products2.add(productOption33);

        product2.addProductOptionList(products2);

        List<Product> productList = new ArrayList<>();
        productList.add(product);
        productList.add(product2);

        when(productRepository.findAllBySalseYn("Y")).thenReturn(productList);

        //When
        ProductService productService = new ProductService(productRepository);
        List<ProductResponse.Select> resultProductList = productService.selectSalesProductList();

        //Then
        assertEquals(productId,resultProductList.get(0).productId());
        assertEquals(name,resultProductList.get(0).name());
        assertEquals(description,resultProductList.get(0).description());
        assertEquals(ProductResponse.Select.from(productList).get(0).options(),resultProductList.get(0).options());

        assertEquals(productId2,resultProductList.get(1).productId());
        assertEquals(name2,resultProductList.get(1).name());
        assertEquals(description2,resultProductList.get(1).description());
        assertEquals(ProductResponse.Select.from(productList).get(1).options(),resultProductList.get(1).options());
    }

    @Test
    @DisplayName("[상품 주문]재고 차감")
    void decreaseStock() throws Exception {
        //Given
        long productId = 1L;
        List<Long> productOptionIds = List.of(1L,2L,2L,2L,3L);

        Product product = Product.builder()
                .productId(1L)
                .name("티셔츠")
                .description("티셔츠 설명")
                .build();

        ProductOption productOption1 = new ProductOption(1L,product,"XL",20_000L,30L,15L,"Y", LocalDateTime.now());
        ProductOption productOption2 = new ProductOption(2L,product,"L",20_000L,20L,5L,"Y", LocalDateTime.now());
        ProductOption productOption3 = new ProductOption(3L,product,"M",20_000L,10L,2L,"Y", LocalDateTime.now());

        List<ProductOption> products = new ArrayList<>();
        products.add(productOption1);
        products.add(productOption2);
        products.add(productOption2);
        products.add(productOption2);
        products.add(productOption3);

        product.addProductOptionList(products);

        when(productRepository.findByProductOptionIds(productOptionIds)).thenReturn(products);
        when(productRepository.updateStockQuantity(any(ProductOption.class))).thenAnswer(invocation -> invocation.getArgument(0));
        //When
        ProductService productService = new ProductService(productRepository);
        productService.decreaseStock(productOptionIds);

        //Then
        assertEquals(14L, productOption1.getStockQuantity()); // 15 - 1 = 14
        assertEquals(2L, productOption2.getStockQuantity()); // 5 - 3 = 2
        assertEquals(1L, productOption3.getStockQuantity()); // 2 - 1 = 1

        verify(productRepository, times(1)).findByProductOptionIds(productOptionIds);
        verify(productRepository, times(5)).updateStockQuantity(any(ProductOption.class));
    }

    @Test
    @DisplayName("[상품 주문]총 주문 금액 계산")
    void calculateProductTotalPrice(){
        //Given
        long productId = 1L;

        Product product = Product.builder()
                .productId(1L)
                .name("티셔츠")
                .description("티셔츠 설명")
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
        ProductService productService = new ProductService(productRepository);
        long totalOrderPrice = productService.calculateProductTotalPrice(products);

        //Then
        assertEquals(60_000L,totalOrderPrice);
    }
}