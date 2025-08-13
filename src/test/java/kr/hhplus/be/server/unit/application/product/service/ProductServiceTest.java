package kr.hhplus.be.server.unit.application.product.service;

import kr.hhplus.be.server.application.product.dto.ProductResponse;
import kr.hhplus.be.server.application.product.repository.ProductOptionRepository;
import kr.hhplus.be.server.application.product.repository.ProductRepository;
import kr.hhplus.be.server.application.product.service.ProductService;
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
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
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
        ProductService productService = new ProductService(productRepository,productOptionRepository);
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

        when(productOptionRepository.selectProductOptionByProductOptionIdInWithLock(anyList())).thenReturn(List.of(productOption1, productOption2, productOption3));

        when(productOptionRepository.save(any(ProductOption.class))).thenAnswer(invocation -> invocation.getArgument(0));

        //When
        ProductService productService = new ProductService(productRepository,productOptionRepository);
        // ID별 등장 횟수 계산
        Map<Long, Long> quantityMap = productOptionIds.stream().collect(Collectors.groupingBy(id -> id, Collectors.counting()));

        // 상품 옵션 조회
        List<ProductOption> lockedOptions = productOptionRepository.selectProductOptionByProductOptionIdInWithLock(new ArrayList<>(quantityMap.keySet()));

        // 차감 호출
        for (ProductOption option : lockedOptions) {
            Long quantityToDecrease = quantityMap.get(option.getProductOptionId());
            productService.decreaseStock(option, quantityToDecrease);
        }

        //Then
        assertEquals(14L, productOption1.getStockQuantity()); // 15 - 1 = 14
        assertEquals(2L, productOption2.getStockQuantity()); // 5 - 3 = 2
        assertEquals(1L, productOption3.getStockQuantity()); // 2 - 1 = 1

        verify(productOptionRepository, times(1)).selectProductOptionByProductOptionIdInWithLock(any(List.class));
        verify(productOptionRepository, times(3)).save(any(ProductOption.class));
    }

    @Test
    @DisplayName("[상품 주문]총 주문 금액 계산")
    void calculateProductTotalPrice() throws Exception {
        //Given
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
        ProductService productService = new ProductService(productRepository,productOptionRepository);
        long totalOrderPrice = 0L;
        for (ProductOption productOption : products){
            totalOrderPrice += productService.calculateProductTotalPrice(productOption,1L);
        }

        //Then
        assertEquals(60_000L,totalOrderPrice);
    }

}