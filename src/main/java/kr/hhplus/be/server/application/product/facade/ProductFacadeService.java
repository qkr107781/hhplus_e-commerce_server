package kr.hhplus.be.server.application.product.facade;

import kr.hhplus.be.server.application.order.dto.OrderProductSummary;
import kr.hhplus.be.server.application.order.service.OrderProductService;
import kr.hhplus.be.server.application.order.service.OrderService;
import kr.hhplus.be.server.application.product.dto.ProductResponse;
import kr.hhplus.be.server.application.product.service.ProductStatisticsService;
import kr.hhplus.be.server.application.product.service.ProductStatisticsUseCase;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderProduct;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductFacadeService implements ProductStatisticsUseCase {

    private final OrderService orderService;
    private final OrderProductService orderProductService;
    private final ProductStatisticsService productStatisticsService;

    public ProductFacadeService(OrderService orderService, OrderProductService orderProductService, ProductStatisticsService productStatisticsService) {
        this.orderService = orderService;
        this.orderProductService = orderProductService;
        this.productStatisticsService = productStatisticsService;
    }

    /**
     * 오늘 기준 4일 전부터 1일 전까지의 데이터 중 salesQuantity 기준 상위 5개를 조회합니다.
     * 예: 오늘이 7월 25일이면, 7월 21일 부터 7월 24일 까지의 데이터를 조회합니다.
     * @return 상위 5개 통계 데이터 리스트
     */
    @Override
    public List<ProductResponse.Statistics> selectTop5SalesProductBySpecificRange() {
        List<OrderProduct> orderProductListByBefore3Days = new ArrayList<>();

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = LocalDate.now().minusDays(3);

        List<Order> orderList = orderService.selectOrderByOrderStatusAndOrderDateBetween("complete_payment", startDate, endDate);

        //추출일 기준 3일전~1일전 결제 완료된 주문의 주문 상품 조회
        for (Order order : orderList){
            List<OrderProduct> orderProductList = orderProductService.selectOrderProductsByOrderId(order.getOrderId());
            orderProductListByBefore3Days.addAll(orderProductList);
        }

        List<OrderProductSummary> top5OrderProductList = orderProductService.getTop5OrderProduct(orderProductListByBefore3Days);

        return productStatisticsService.selectTop5SalesProductBySpecificRange(top5OrderProductList);
    }

}
