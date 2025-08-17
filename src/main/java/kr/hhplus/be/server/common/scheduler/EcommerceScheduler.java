package kr.hhplus.be.server.common.scheduler;

import kr.hhplus.be.server.application.order.dto.OrderProductSummary;
import kr.hhplus.be.server.application.order.service.OrderProductService;
import kr.hhplus.be.server.application.order.service.OrderService;
import kr.hhplus.be.server.application.product.dto.ProductResponse;
import kr.hhplus.be.server.application.product.service.ProductStatisticsService;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderProduct;
import org.springframework.cache.annotation.CachePut;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class EcommerceScheduler {

    private final OrderService orderService;
    private final OrderProductService orderProductService;
    private final ProductStatisticsService productStatisticsService;

    public EcommerceScheduler(OrderService orderService, OrderProductService orderProductService, ProductStatisticsService productStatisticsService) {
        this.orderService = orderService;
        this.orderProductService = orderProductService;
        this.productStatisticsService = productStatisticsService;
    }

    private static final String CACHE_NAME = "topSalesProducts";
    private static final String CACHE_KEY = "'top5:sales:last3days'";

    @CachePut(
            cacheNames = CACHE_NAME,
            key = CACHE_KEY
    )
    @Scheduled(cron = "0 0 0 * * *") // 매일 0:00 실행
    public List<ProductResponse.Statistics> productStatisticsCache() {
        List<OrderProduct> orderProductListByBefore3Days = new ArrayList<>();

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = LocalDate.now().minusDays(3);

        List<Order> orderList = orderService.selectOrderByOrderStatusAndOrderDateBetween("complete_payment", startDate, endDate);

        //추출일 기준 3일전~1일전 결제 완료된 주문의 주문 상품 조회
        for (Order order : orderList){
            List<OrderProduct> orderProductList = orderProductService.selectOrderProductsByOrderIdOrderByProductOptionIdAsc(order.getOrderId());
            orderProductListByBefore3Days.addAll(orderProductList);
        }

        List<OrderProductSummary> top5OrderProductList = orderProductService.getTop5OrderProduct(orderProductListByBefore3Days);

        return productStatisticsService.selectTop5SalesProductBySpecificRange(top5OrderProductList);
    }
}
