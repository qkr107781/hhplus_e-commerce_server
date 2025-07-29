package kr.hhplus.be.server.application.product.service;

import kr.hhplus.be.server.application.product.dto.ProductResponse;
import kr.hhplus.be.server.application.product.repository.ProductRepository;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductOption;
import kr.hhplus.be.server.domain.product.ProductStatistics;
import kr.hhplus.be.server.domain.product.ProductStock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
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

        long productId2 = 2L;
        String name2 = "티셔츠";

        Product product = Product.builder()
                .productId(productId)
                .name(name)
                .build();

        Product product2 = Product.builder()
                .productId(productId2)
                .name(name2)
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

        // 상품 옵션 세팅 (재고 차감 전의 초기 상태)
        ProductOption productOption11 = ProductOption.builder()
                .productOptionId(4L)
                .product(product)
                .optionName("XL")
                .price(20_000L)
                .salesYn("Y")
                .regDate(LocalDateTime.now())
                .build();
        ProductOption productOption22 = ProductOption.builder()
                .productOptionId(5L)
                .product(product)
                .optionName("L")
                .price(20_000L)
                .salesYn("Y")
                .regDate(LocalDateTime.now())
                .build();
        ProductOption productOption33 = ProductOption.builder()
                .productOptionId(6L)
                .product(product)
                .optionName("M")
                .price(20_000L)
                .salesYn("Y")
                .regDate(LocalDateTime.now())
                .build();

        // 상품 재고
        ProductStock productStock11 = ProductStock.builder()
                .productStockId(4L)
                .totalQuantity(30L)
                .stockQuantity(20L)
                .productOption(productOption1)
                .build();

        ProductStock productStock22 = ProductStock.builder()
                .productStockId(5L)
                .totalQuantity(30L)
                .stockQuantity(20L)
                .productOption(productOption2)
                .build();

        ProductStock productStock33 = ProductStock.builder()
                .productStockId(6L)
                .totalQuantity(30L)
                .stockQuantity(20L)
                .productOption(productOption3)
                .build();

        productOption1.addProductStock(productStock1);
        productOption2.addProductStock(productStock2);
        productOption3.addProductStock(productStock3);
        List<ProductOption> products2 = new ArrayList<>();
        products2.add(productOption11);
        products2.add(productOption22);
        products2.add(productOption33);

        product2.addProductOptionList(products2);

        List<Product> productList = new ArrayList<>();
        productList.add(product);
        productList.add(product2);

        when(productRepository.findByProductOptions_SalesYn("Y")).thenReturn(productList);

        //When
        ProductService productService = new ProductService(productRepository);
        List<ProductResponse.Select> resultProductList = productService.selectSalesProductList();

        //Then
        assertEquals(productId,resultProductList.get(0).productId());
        assertEquals(name,resultProductList.get(0).name());
        assertEquals(ProductResponse.Select.from(productList).get(0).options(),resultProductList.get(0).options());

        assertEquals(productId2,resultProductList.get(1).productId());
        assertEquals(name2,resultProductList.get(1).name());
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
                .stockQuantity(15L)
                .productOption(productOption1)
                .build();

        ProductStock productStock2 = ProductStock.builder()
                .productStockId(2L)
                .totalQuantity(30L)
                .stockQuantity(5L)
                .productOption(productOption2)
                .build();

        ProductStock productStock3 = ProductStock.builder()
                .productStockId(3L)
                .totalQuantity(30L)
                .stockQuantity(2L)
                .productOption(productOption3)
                .build();

        productOption1.addProductStock(productStock1);
        productOption2.addProductStock(productStock2);
        productOption3.addProductStock(productStock3);
        List<ProductOption> products = new ArrayList<>();
        products.add(productOption1);
        products.add(productOption2);
        products.add(productOption2);
        products.add(productOption2);
        products.add(productOption3);

        product.addProductOptionList(products);

        when(productRepository.findByProductOptionsIn(productOptionIds)).thenReturn(products);
        when(productRepository.save(any(ProductOption.class))).thenAnswer(invocation -> invocation.getArgument(0));
        //When
        ProductService productService = new ProductService(productRepository);
        productService.decreaseStock(productOptionIds);

        //Then
        assertEquals(14L, productOption1.getProductStock().getStockQuantity()); // 15 - 1 = 14
        assertEquals(2L, productOption2.getProductStock().getStockQuantity()); // 5 - 3 = 2
        assertEquals(1L, productOption3.getProductStock().getStockQuantity()); // 2 - 1 = 1

        verify(productRepository, times(1)).findByProductOptionsIn(productOptionIds);
        verify(productRepository, times(5)).save(any(ProductOption.class));
    }

    @Test
    @DisplayName("[상품 주문]총 주문 금액 계산")
    void calculateProductTotalPrice() throws Exception {
        //Given
        long productId = 1L;

        Product product = Product.builder()
                .productId(1L)
                .name("티셔츠")
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
        ProductService productService = new ProductService(productRepository);
        long totalOrderPrice = productService.calculateProductTotalPrice(products);

        //Then
        assertEquals(60_000L,totalOrderPrice);
    }

    @Test
    @DisplayName("[상품 통계]지난 3일간 가장 많이 팔린 TOP5 상품 조회")
    void productStatistics(){
        //Given
        ProductStatistics productStatistics1 = ProductStatistics.builder()
                .statisticsId(1L)
                .productId(1L)
                .productName("티셔츠")
                .salesQuantity(25L)
                .selectionDate(LocalDate.now().minusDays(2))
                .build();

        ProductStatistics productStatistics2 = ProductStatistics.builder()
                .statisticsId(2L)
                .productId(2L)
                .productName("티셔츠")
                .salesQuantity(10L)
                .selectionDate(LocalDate.now().minusDays(2))
                .build();

        ProductStatistics productStatistics3 = ProductStatistics.builder()
                .statisticsId(3L)
                .productId(3L)
                .productName("티셔츠")
                .salesQuantity(20L)
                .selectionDate(LocalDate.now().minusDays(3))
                .build();

        ProductStatistics productStatistics4 = ProductStatistics.builder()
                .statisticsId(4L)
                .productId(4L)
                .productName("티셔츠")
                .salesQuantity(5L)
                .selectionDate(LocalDate.now().minusDays(3))
                .build();

        ProductStatistics productStatistics5 = ProductStatistics.builder()
                .statisticsId(5L)
                .productId(5L)
                .productName("티셔츠")
                .salesQuantity(13L)
                .selectionDate(LocalDate.now().minusDays(4))
                .build();

        List<ProductStatistics> productStatisticsList = new ArrayList<>();
        productStatisticsList.add(productStatistics1);
        productStatisticsList.add(productStatistics2);
        productStatisticsList.add(productStatistics3);
        productStatisticsList.add(productStatistics4);
        productStatisticsList.add(productStatistics5);

        productStatisticsList.sort(Comparator.comparing(ProductStatistics::getSalesQuantity).reversed());

        LocalDate today = LocalDate.now();
        LocalDateTime startDate = today.minusDays(4).atStartOfDay();
        LocalDateTime endDate = today.minusDays(1).atStartOfDay().plusDays(1).minusNanos(1);

        when(productRepository.findTop5BySelectionDateRangeOrderBySalesQuantityDesc(startDate,endDate)).thenReturn(productStatisticsList);

        //When
        ProductService productService = new ProductService(productRepository);
        List<ProductStatistics> resultProductStatisticsList = productService.selectTop5SalesStatisticsSpecificRange();

        //Then
        //1 - 3 - 5 - 2 -4
        assertEquals(1L,resultProductStatisticsList.get(0).getProductId());
        assertEquals(3L,resultProductStatisticsList.get(1).getProductId());
        assertEquals(5L,resultProductStatisticsList.get(2).getProductId());
        assertEquals(2L,resultProductStatisticsList.get(3).getProductId());
        assertEquals(4L,resultProductStatisticsList.get(4).getProductId());
    }
}