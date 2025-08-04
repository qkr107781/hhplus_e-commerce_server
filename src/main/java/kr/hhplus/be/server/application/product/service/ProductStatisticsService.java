package kr.hhplus.be.server.application.product.service;

import kr.hhplus.be.server.application.order.dto.OrderProductSummary;
import kr.hhplus.be.server.application.product.dto.ProductResponse;
import kr.hhplus.be.server.application.product.repository.ProductStatisticsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductStatisticsService {

    private final ProductStatisticsRepository productStatisticsRepository;

    public ProductStatisticsService(ProductStatisticsRepository productStatisticsRepository) {
        this.productStatisticsRepository = productStatisticsRepository;
    }

    public List<ProductResponse.Statistics> selectTop5SalesProductBySpecificRange(List<OrderProductSummary> orderProductList) {
        return ProductResponse.Statistics.from(productStatisticsRepository.selectTop5SalseProductByLast3Days(orderProductList));
    }

}
