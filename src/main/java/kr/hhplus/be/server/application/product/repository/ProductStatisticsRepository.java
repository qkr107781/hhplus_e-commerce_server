package kr.hhplus.be.server.application.product.repository;

import kr.hhplus.be.server.application.order.dto.OrderProductSummary;
import kr.hhplus.be.server.domain.product.ProductStatistics;

import java.util.List;

public interface ProductStatisticsRepository {

    List<ProductStatistics> selectTop5SalseProductByLast3Days(List<OrderProductSummary> orderProductList);

}
