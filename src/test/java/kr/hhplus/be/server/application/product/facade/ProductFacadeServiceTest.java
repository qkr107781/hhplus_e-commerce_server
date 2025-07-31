package kr.hhplus.be.server.application.product.facade;

import kr.hhplus.be.server.application.order.dto.OrderProductSummary;
import kr.hhplus.be.server.application.order.service.OrderProductService;
import kr.hhplus.be.server.application.order.service.OrderService;
import kr.hhplus.be.server.application.product.dto.ProductResponse;
import kr.hhplus.be.server.application.product.service.ProductStatisticsService;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderProduct;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.MockedStatic;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductFacadeServiceTest {

    @Mock
    OrderService orderService;

    @Mock
    OrderProductService orderProductService;

    @Mock
    ProductStatisticsService productStatisticsService;

    @InjectMocks
    ProductFacadeService productFacadeService;

    @Test
    @DisplayName("[상품 통계]지난 3일간 가장 많이 팔린 TOP5 상품 조회")
    void productStatistics(){
        LocalDate fixedDateForTest = LocalDate.of(2025, 7, 31);
        LocalDateTime fixedDateTimeForTest = fixedDateForTest.atStartOfDay();

        try (MockedStatic<LocalDate> mockedLocalDate = mockStatic(LocalDate.class)) {
            mockedLocalDate.when(LocalDate::now).thenReturn(fixedDateForTest);

            LocalDate startDateInService = fixedDateForTest.minusDays(3);
            LocalDate endDateInService = fixedDateForTest;

            List<Order> ordersForFacade = new ArrayList<>();
            ordersForFacade.add(Order.builder().orderId(3L).orderStatus("complete_payment").orderDate(fixedDateTimeForTest.minusDays(4)).build());
            ordersForFacade.add(Order.builder().orderId(4L).orderStatus("complete_payment").orderDate(fixedDateTimeForTest.minusDays(3)).build());
            ordersForFacade.add(Order.builder().orderId(5L).orderStatus("complete_payment").orderDate(fixedDateTimeForTest.minusDays(3)).build());
            ordersForFacade.add(Order.builder().orderId(6L).orderStatus("complete_payment").orderDate(fixedDateTimeForTest.minusDays(2)).build());
            ordersForFacade.add(Order.builder().orderId(7L).orderStatus("complete_payment").orderDate(fixedDateTimeForTest.minusDays(2)).build());
            ordersForFacade.add(Order.builder().orderId(8L).orderStatus("complete_payment").orderDate(fixedDateTimeForTest.minusDays(1)).build());

            when(orderService.selectOrderByOrderStatusAndOrderDateBetween(
                    eq("complete_payment"), eq(startDateInService), eq(endDateInService)))
                    .thenReturn(ordersForFacade);

            when(orderProductService.selectOrderProductsByOrderId(3L)).thenReturn(List.of(
                    OrderProduct.builder().orderId(3L).productOptionId(7L).productQuantity(12L).build(),
                    OrderProduct.builder().orderId(3L).productOptionId(4L).productQuantity(40L).build(),
                    OrderProduct.builder().orderId(3L).productOptionId(5L).productQuantity(41L).build()
            ));

            when(orderProductService.selectOrderProductsByOrderId(4L)).thenReturn(List.of(
                    OrderProduct.builder().orderId(4L).productOptionId(7L).productQuantity(11L).build(),
                    OrderProduct.builder().orderId(4L).productOptionId(8L).productQuantity(21L).build(),
                    OrderProduct.builder().orderId(4L).productOptionId(9L).productQuantity(31L).build()
            ));

            when(orderProductService.selectOrderProductsByOrderId(5L)).thenReturn(List.of(
                    OrderProduct.builder().orderId(5L).productOptionId(7L).productQuantity(11L).build(),
                    OrderProduct.builder().orderId(5L).productOptionId(8L).productQuantity(21L).build(),
                    OrderProduct.builder().orderId(5L).productOptionId(9L).productQuantity(31L).build()
            ));

            when(orderProductService.selectOrderProductsByOrderId(6L)).thenReturn(List.of(
                    OrderProduct.builder().orderId(6L).productOptionId(4L).productQuantity(10L).build(),
                    OrderProduct.builder().orderId(6L).productOptionId(5L).productQuantity(20L).build(),
                    OrderProduct.builder().orderId(6L).productOptionId(6L).productQuantity(30L).build()
            ));

            when(orderProductService.selectOrderProductsByOrderId(7L)).thenReturn(List.of(
                    OrderProduct.builder().orderId(7L).productOptionId(4L).productQuantity(10L).build(),
                    OrderProduct.builder().orderId(7L).productOptionId(5L).productQuantity(20L).build(),
                    OrderProduct.builder().orderId(7L).productOptionId(6L).productQuantity(30L).build()
            ));

            List<OrderProductSummary> expectedTop5SummariesCalculatedByFacade = new ArrayList<>();
            expectedTop5SummariesCalculatedByFacade.add(new OrderProductSummary(5L, 81L));
            expectedTop5SummariesCalculatedByFacade.add(new OrderProductSummary(9L, 62L));
            expectedTop5SummariesCalculatedByFacade.add(new OrderProductSummary(4L, 60L));
            expectedTop5SummariesCalculatedByFacade.add(new OrderProductSummary(6L, 60L));
            expectedTop5SummariesCalculatedByFacade.add(new OrderProductSummary(8L, 42L));

            expectedTop5SummariesCalculatedByFacade.sort(Comparator
                    .comparing(OrderProductSummary::totalOrderedQuantity, Comparator.reverseOrder())
                    .thenComparing(OrderProductSummary::productOptionId));

            when(orderProductService.getTop5OrderProduct(anyList())).thenReturn(expectedTop5SummariesCalculatedByFacade);

            List<ProductResponse.Statistics> expectedResultList = new ArrayList<>();
            expectedResultList.add(new ProductResponse.Statistics( "반바지", 81L));
            expectedResultList.add(new ProductResponse.Statistics( "반팔", 62L));
            expectedResultList.add(new ProductResponse.Statistics( "나시", 60L));
            expectedResultList.add(new ProductResponse.Statistics( "긴바지", 60L));
            expectedResultList.add(new ProductResponse.Statistics( "긴팔", 42L));

            ArgumentCaptor<List> argumentCaptor = ArgumentCaptor.forClass(List.class);
            when(productStatisticsService.selectTop5SalesProductBySpecificRange(argumentCaptor.capture()))
                    .thenReturn(expectedResultList);

            List<ProductResponse.Statistics> actualResult = productFacadeService.selectTop5SalesProductBySpecificRange();

            List<OrderProductSummary> capturedArgument = argumentCaptor.getValue();
            assertEquals(expectedTop5SummariesCalculatedByFacade.size(), capturedArgument.size());
            for (int i = 0; i < expectedTop5SummariesCalculatedByFacade.size(); i++) {
                assertEquals(expectedTop5SummariesCalculatedByFacade.get(i).productOptionId(), capturedArgument.get(i).productOptionId());
                assertEquals(expectedTop5SummariesCalculatedByFacade.get(i).totalOrderedQuantity(), capturedArgument.get(i).totalOrderedQuantity());
            }

            assertEquals(5, actualResult.size());

            assertEquals("반바지", actualResult.get(0).productName());
            assertEquals(81L, actualResult.get(0).salesQuantity());

            assertEquals("반팔", actualResult.get(1).productName());
            assertEquals(62L, actualResult.get(1).salesQuantity());

            assertEquals("나시", actualResult.get(2).productName());
            assertEquals(60L, actualResult.get(2).salesQuantity());

            assertEquals("긴바지", actualResult.get(3).productName());
            assertEquals(60L, actualResult.get(3).salesQuantity());

            assertEquals("긴팔", actualResult.get(4).productName());
            assertEquals(42L, actualResult.get(4).salesQuantity());
        }
    }
}