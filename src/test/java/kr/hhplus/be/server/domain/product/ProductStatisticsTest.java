package kr.hhplus.be.server.domain.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductStatisticsTest {

    @Test
    @DisplayName("")
    void selectSalesTop5Product(){
        //Given
        long productId = 1L;
        String productName = "티셔츠";

        long productOptionId1 = 1L;
        long productOptionId2 = 2L;
        long productOptionId3 = 3L;
        long productOptionId4 = 4L;
        long productOptionId5 = 5L;

        String productOptionName1 = "XXL";
        String productOptionName2 = "XL";
        String productOptionName3 = "L";
        String productOptionName4 = "M";
        String productOptionName5 = "S";

        long price1 = 10_000L;
        long price2 = 20_000L;
        long price3 = 30_000L;
        long price4 = 40_000L;
        long price5 = 50_000L;

        long salesQuantity1 = 5L;
        long salesQuantity2 = 4L;
        long salesQuantity3 = 3L;
        long salesQuantity4 = 2L;
        long salesQuantity5 = 1L;

        long ranking1 = 1L;
        long ranking2 = 2L;
        long ranking3 = 3L;
        long ranking4 = 4L;
        long ranking5 = 5L;

        LocalDateTime selectionDate = LocalDateTime.now();

        //When
        ProductStatistics productStatistics1 = new ProductStatistics(1L,productId,productOptionId1,productName,productOptionName1,price1,salesQuantity1,ranking1,selectionDate);
        ProductStatistics productStatistics2 = new ProductStatistics(1L,productId,productOptionId2,productName,productOptionName2,price2,salesQuantity2,ranking2,selectionDate);
        ProductStatistics productStatistics3 = new ProductStatistics(1L,productId,productOptionId3,productName,productOptionName3,price3,salesQuantity3,ranking3,selectionDate);
        ProductStatistics productStatistics4 = new ProductStatistics(1L,productId,productOptionId4,productName,productOptionName4,price4,salesQuantity4,ranking4,selectionDate);
        ProductStatistics productStatistics5 = new ProductStatistics(1L,productId,productOptionId5,productName,productOptionName5,price5,salesQuantity5,ranking5,selectionDate);

        List<ProductStatistics> productStatisticsList = new ArrayList<>();
        productStatisticsList.add(productStatistics1);
        productStatisticsList.add(productStatistics2);
        productStatisticsList.add(productStatistics3);
        productStatisticsList.add(productStatistics4);
        productStatisticsList.add(productStatistics5);

        //Then
        assertEquals(productOptionId1,productStatisticsList.get(0).getProductOptionId());
        assertEquals(productOptionName1,productStatisticsList.get(0).getProductOptionName());
        assertEquals(price1,productStatisticsList.get(0).getPrice());
        assertEquals(salesQuantity1,productStatisticsList.get(0).getSalesQuantity());
        assertEquals(ranking1,productStatisticsList.get(0).getRanking());

        assertEquals(productOptionId2,productStatisticsList.get(1).getProductOptionId());
        assertEquals(productOptionName2,productStatisticsList.get(1).getProductOptionName());
        assertEquals(price2,productStatisticsList.get(1).getPrice());
        assertEquals(salesQuantity2,productStatisticsList.get(1).getSalesQuantity());
        assertEquals(ranking2,productStatisticsList.get(1).getRanking());

        assertEquals(productOptionId3,productStatisticsList.get(2).getProductOptionId());
        assertEquals(productOptionName3,productStatisticsList.get(2).getProductOptionName());
        assertEquals(price3,productStatisticsList.get(2).getPrice());
        assertEquals(salesQuantity3,productStatisticsList.get(2).getSalesQuantity());
        assertEquals(ranking3,productStatisticsList.get(2).getRanking());

        assertEquals(productOptionId4,productStatisticsList.get(3).getProductOptionId());
        assertEquals(productOptionName4,productStatisticsList.get(3).getProductOptionName());
        assertEquals(price4,productStatisticsList.get(3).getPrice());
        assertEquals(salesQuantity4,productStatisticsList.get(3).getSalesQuantity());
        assertEquals(ranking4,productStatisticsList.get(3).getRanking());

        assertEquals(productOptionId5,productStatisticsList.get(4).getProductOptionId());
        assertEquals(productOptionName5,productStatisticsList.get(4).getProductOptionName());
        assertEquals(price5,productStatisticsList.get(4).getPrice());
        assertEquals(salesQuantity5,productStatisticsList.get(4).getSalesQuantity());
        assertEquals(ranking5,productStatisticsList.get(4).getRanking());

        assertEquals(5,productStatisticsList.size());
    }

}
