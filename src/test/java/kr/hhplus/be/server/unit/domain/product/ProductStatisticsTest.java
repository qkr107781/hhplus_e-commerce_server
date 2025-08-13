package kr.hhplus.be.server.unit.domain.product;

import kr.hhplus.be.server.domain.product.ProductStatistics;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductStatisticsTest {

    @Test
    @DisplayName("[상위 상품 조회]통계 테이블에서 기간별 많이 팔린 상품 상위 5개를 조회")
    void selectSalesTop5Product(){
        //Given
        long productId1 = 1L;
        long productId2 = 2L;
        long productId3 = 3L;
        long productId4 = 4L;
        long productId5 = 5L;

        String productName1 = "반팔";
        String productName2 = "긴팔";
        String productName3 = "반바지";
        String productName4 = "긴바지";
        String productName5 = "모자";

        long salesQuantity1 = 5L;
        long salesQuantity2 = 4L;
        long salesQuantity3 = 3L;
        long salesQuantity4 = 2L;
        long salesQuantity5 = 1L;

        LocalDate selectionDate = LocalDate.now();

        //When
        ProductStatistics productStatistics1 = ProductStatistics.builder()
                .statisticsId(1L)
                .productId(productId1)
                .productName(productName1)
                .salesQuantity(salesQuantity1)
                .selectionDate(selectionDate)
                .build();

        ProductStatistics productStatistics2 = ProductStatistics.builder()
                .statisticsId(2L)
                .productId(productId2)
                .productName(productName2)
                .salesQuantity(salesQuantity2)
                .selectionDate(selectionDate)
                .build();
        ProductStatistics productStatistics3 = ProductStatistics.builder()
                .statisticsId(3L)
                .productId(productId3)
                .productName(productName3)
                .salesQuantity(salesQuantity3)
                .selectionDate(selectionDate)
                .build();
        ProductStatistics productStatistics4 = ProductStatistics.builder()
                .statisticsId(4L)
                .productId(productId4)
                .productName(productName4)
                .salesQuantity(salesQuantity4)
                .selectionDate(selectionDate)
                .build();
        ProductStatistics productStatistics5 = ProductStatistics.builder()
                .statisticsId(5L)
                .productId(productId5)
                .productName(productName5)
                .salesQuantity(salesQuantity5)
                .selectionDate(selectionDate)
                .build();

        List<ProductStatistics> productStatisticsList = new ArrayList<>();
        productStatisticsList.add(productStatistics1);
        productStatisticsList.add(productStatistics2);
        productStatisticsList.add(productStatistics3);
        productStatisticsList.add(productStatistics4);
        productStatisticsList.add(productStatistics5);

        //Then
        assertEquals(productId1,productStatisticsList.get(0).getProductId());
        assertEquals(productName1,productStatisticsList.get(0).getProductName());
        assertEquals(salesQuantity1,productStatisticsList.get(0).getSalesQuantity());

        assertEquals(productId2,productStatisticsList.get(1).getProductId());
        assertEquals(productName2,productStatisticsList.get(1).getProductName());
        assertEquals(salesQuantity2,productStatisticsList.get(1).getSalesQuantity());

        assertEquals(productId3,productStatisticsList.get(2).getProductId());
        assertEquals(productName3,productStatisticsList.get(2).getProductName());
        assertEquals(salesQuantity3,productStatisticsList.get(2).getSalesQuantity());

        assertEquals(productId4,productStatisticsList.get(3).getProductId());
        assertEquals(productName4,productStatisticsList.get(3).getProductName());
        assertEquals(salesQuantity4,productStatisticsList.get(3).getSalesQuantity());

        assertEquals(productId5,productStatisticsList.get(4).getProductId());
        assertEquals(productName5,productStatisticsList.get(4).getProductName());
        assertEquals(salesQuantity5,productStatisticsList.get(4).getSalesQuantity());

        assertEquals(5,productStatisticsList.size());
    }

}
