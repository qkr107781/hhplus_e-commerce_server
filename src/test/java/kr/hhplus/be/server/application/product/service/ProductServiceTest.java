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

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    ProductRepository productRepository;

    @Test
    @DisplayName("")
    void selectSalesProductList(){
        //Given
        long productId = 1L;
        String name = "티셔츠";
        String description = "티셔츠 설명";

        ProductOption productOption1 = new ProductOption(1L,productId,"XL",20_000L,30L,15L,"Y", LocalDateTime.now());
        ProductOption productOption2 = new ProductOption(2L,productId,"L",20_000L,20L,5L,"Y", LocalDateTime.now());
        ProductOption productOption3 = new ProductOption(3L,productId,"M",20_000L,10L,2L,"Y", LocalDateTime.now());

        List<ProductOption> products = new ArrayList<>();
        products.add(productOption1);
        products.add(productOption2);
        products.add(productOption3);

        long productId2 = 2L;
        String name2 = "티셔츠";
        String description2 = "티셔츠 설명";

        ProductOption productOption11 = new ProductOption(4L,productId2,"XL",20_000L,30L,15L,"Y", LocalDateTime.now());
        ProductOption productOption22 = new ProductOption(5L,productId2,"L",20_000L,20L,5L,"Y", LocalDateTime.now());
        ProductOption productOption33 = new ProductOption(6L,productId2,"M",20_000L,10L,2L,"Y", LocalDateTime.now());

        List<ProductOption> products2 = new ArrayList<>();
        products2.add(productOption11);
        products2.add(productOption22);
        products2.add(productOption33);

        Product product = new Product(productId,name,description,products);
        Product product2 = new Product(productId2,name2,description2,products2);

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
}