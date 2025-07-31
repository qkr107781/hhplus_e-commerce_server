package kr.hhplus.be.server.application.product.repository;

import kr.hhplus.be.server.domain.product.ProductStatistics;

import java.time.LocalDate;
import java.util.List;

public interface ProductStatisticsRepository {

    List<ProductStatistics> findTop5BySelectionDateBetweenOrderBySalesQuantityDesc(LocalDate startDate, LocalDate endDate);

}
