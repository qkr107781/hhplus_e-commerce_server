package kr.hhplus.be.server.persistence.product;

import kr.hhplus.be.server.application.product.repository.ProductStatisticsRepository;
import kr.hhplus.be.server.domain.product.ProductStatistics;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class ProductStatisticsAdapter implements ProductStatisticsRepository {

    private final ProductStatisticsJpaRepository productStatisticsJpaRepository;

    public ProductStatisticsAdapter(ProductStatisticsJpaRepository productStatisticsJpaRepository) {
        this.productStatisticsJpaRepository = productStatisticsJpaRepository;
    }


    @Override
    public List<ProductStatistics> findTop5BySelectionDateBetweenOrderBySalesQuantityDesc(LocalDate startDate, LocalDate endDate) {
        return productStatisticsJpaRepository.findTop5BySelectionDateBetweenOrderBySalesQuantityDesc(startDate,endDate);
    }
}
