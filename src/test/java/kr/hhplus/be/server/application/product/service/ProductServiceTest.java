package kr.hhplus.be.server.application.product.service;

import kr.hhplus.be.server.application.product.dto.ProductResponse;
import kr.hhplus.be.server.application.product.repository.ProductOptionRepository;
import kr.hhplus.be.server.application.product.repository.ProductRepository;
import kr.hhplus.be.server.application.product.repository.ProductStatisticsRepository;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductOption;
import kr.hhplus.be.server.domain.product.ProductStatistics;
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

    @Mock
    ProductOptionRepository productOptionRepository;

    @Mock
    ProductStatisticsRepository productStatisticsRepository;

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
                .productId(product.getProductId())
                .optionName("XL")
                .price(20_000L)
                .salesYn("Y")
                .totalQuantity(30L)
                .stockQuantity(20L)
                .regDate(LocalDateTime.now())
                .build();
        ProductOption productOption2 = ProductOption.builder()
                .productOptionId(2L)
                .productId(product.getProductId())
                .optionName("L")
                .price(20_000L)
                .salesYn("Y")
                .totalQuantity(30L)
                .stockQuantity(20L)
                .regDate(LocalDateTime.now())
                .build();
        ProductOption productOption3 = ProductOption.builder()
                .productOptionId(3L)
                .productId(product.getProductId())
                .optionName("M")
                .price(20_000L)
                .salesYn("Y")
                .totalQuantity(30L)
                .stockQuantity(20L)
                .regDate(LocalDateTime.now())
                .build();
        List<ProductOption> products = new ArrayList<>();
        products.add(productOption1);
        products.add(productOption2);
        products.add(productOption3);

        // 상품 옵션 세팅 (재고 차감 전의 초기 상태)
        ProductOption productOption11 = ProductOption.builder()
                .productOptionId(4L)
                .productId(product.getProductId())
                .optionName("XL")
                .price(20_000L)
                .salesYn("Y")
                .totalQuantity(30L)
                .stockQuantity(20L)
                .regDate(LocalDateTime.now())
                .build();
        ProductOption productOption22 = ProductOption.builder()
                .productOptionId(5L)
                .productId(product.getProductId())
                .optionName("L")
                .price(20_000L)
                .salesYn("Y")
                .totalQuantity(30L)
                .stockQuantity(20L)
                .regDate(LocalDateTime.now())
                .build();
        ProductOption productOption33 = ProductOption.builder()
                .productOptionId(6L)
                .productId(product.getProductId())
                .optionName("M")
                .price(20_000L)
                .salesYn("Y")
                .totalQuantity(30L)
                .stockQuantity(20L)
                .regDate(LocalDateTime.now())
                .build();
        List<ProductOption> products2 = new ArrayList<>();
        products2.add(productOption11);
        products2.add(productOption22);
        products2.add(productOption33);

        List<ProductOption> productExpect = new ArrayList<>();
        productExpect.add(productOption1);
        productExpect.add(productOption2);
        productExpect.add(productOption3);
        productExpect.add(productOption11);
        productExpect.add(productOption22);
        productExpect.add(productOption33);

        List<Product> productList = new ArrayList<>();
        productList.add(product);
        productList.add(product2);

        when(productRepository.selectAllProduct()).thenReturn(productList);
        when(productOptionRepository.selectProductOptionByProductIdAndSalesYn(productList.get(0).getProductId(),"Y")).thenReturn(products);
        when(productOptionRepository.selectProductOptionByProductIdAndSalesYn(productList.get(1).getProductId(),"Y")).thenReturn(products2);

        //When
        ProductService productService = new ProductService(productRepository,productOptionRepository,productStatisticsRepository);
        List<ProductResponse.Select> resultProductList = productService.selectSalesProductList();

        //Then
        assertEquals(productId,resultProductList.get(0).productId());
        assertEquals(name,resultProductList.get(0).name());
        assertEquals(ProductResponse.Select.from(productList,productExpect).get(0).options(),resultProductList.get(0).options());

        assertEquals(productId2,resultProductList.get(1).productId());
        assertEquals(name2,resultProductList.get(1).name());
        assertEquals(ProductResponse.Select.from(productList,productExpect).get(1).options(),resultProductList.get(1).options());
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
                .productId(product.getProductId())
                .optionName("XL")
                .price(20_000L)
                .salesYn("Y")
                .totalQuantity(30L)
                .stockQuantity(15L)
                .regDate(LocalDateTime.now())
                .build();
        ProductOption productOption2 = ProductOption.builder()
                .productOptionId(2L)
                .productId(product.getProductId())
                .optionName("L")
                .price(20_000L)
                .salesYn("Y")
                .totalQuantity(30L)
                .stockQuantity(5L)
                .regDate(LocalDateTime.now())
                .build();
        ProductOption productOption3 = ProductOption.builder()
                .productOptionId(3L)
                .productId(product.getProductId())
                .optionName("M")
                .price(20_000L)
                .salesYn("Y")
                .totalQuantity(30L)
                .stockQuantity(2L)
                .regDate(LocalDateTime.now())
                .build();
        List<ProductOption> products = new ArrayList<>();
        products.add(productOption1);
        products.add(productOption2);
        products.add(productOption2);
        products.add(productOption2);
        products.add(productOption3);

        when(productOptionRepository.selectProductOptionListByProductOptionIds(productOptionIds)).thenReturn(products);
        when(productOptionRepository.save(any(ProductOption.class))).thenAnswer(invocation -> invocation.getArgument(0));
        //When
        ProductService productService = new ProductService(productRepository,productOptionRepository,productStatisticsRepository);
        productService.decreaseStock(productOptionIds);

        //Then
        assertEquals(14L, productOption1.getStockQuantity()); // 15 - 1 = 14
        assertEquals(2L, productOption2.getStockQuantity()); // 5 - 3 = 2
        assertEquals(1L, productOption3.getStockQuantity()); // 2 - 1 = 1

        verify(productOptionRepository, times(1)).selectProductOptionListByProductOptionIds(productOptionIds);
        verify(productOptionRepository, times(5)).save(any(ProductOption.class));
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
                .productId(product.getProductId())
                .optionName("XL")
                .price(20_000L)
                .salesYn("Y")
                .totalQuantity(30L)
                .stockQuantity(20L)
                .regDate(LocalDateTime.now())
                .build();
        ProductOption productOption2 = ProductOption.builder()
                .productOptionId(2L)
                .productId(product.getProductId())
                .optionName("L")
                .price(20_000L)
                .salesYn("Y")
                .totalQuantity(30L)
                .stockQuantity(20L)
                .regDate(LocalDateTime.now())
                .build();
        ProductOption productOption3 = ProductOption.builder()
                .productOptionId(3L)
                .productId(product.getProductId())
                .optionName("M")
                .price(20_000L)
                .salesYn("Y")
                .totalQuantity(30L)
                .stockQuantity(20L)
                .regDate(LocalDateTime.now())
                .build();

        List<ProductOption> products = new ArrayList<>();
        products.add(productOption1);
        products.add(productOption2);
        products.add(productOption3);

        //When
        ProductService productService = new ProductService(productRepository,productOptionRepository,productStatisticsRepository);
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

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(4);

        when(productStatisticsRepository.findTop5BySelectionDateBetweenOrderBySalesQuantityDesc(startDate,endDate)).thenReturn(productStatisticsList);

        //When
        ProductService productService = new ProductService(productRepository,productOptionRepository,productStatisticsRepository);
        List<ProductResponse.Statistics> resultProductStatisticsList = productService.selectTop5SalesStatisticsSpecificRange();

        //Then
        //1 - 3 - 5 - 2 -4
        assertEquals(1L,resultProductStatisticsList.get(0).productId());
        assertEquals(3L,resultProductStatisticsList.get(1).productId());
        assertEquals(5L,resultProductStatisticsList.get(2).productId());
        assertEquals(2L,resultProductStatisticsList.get(3).productId());
        assertEquals(4L,resultProductStatisticsList.get(4).productId());
    }
}