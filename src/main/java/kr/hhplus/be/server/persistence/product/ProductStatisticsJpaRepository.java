package kr.hhplus.be.server.persistence.product;

import kr.hhplus.be.server.domain.product.ProductStatistics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ProductStatisticsJpaRepository extends JpaRepository<ProductStatistics,Long> {

    List<ProductStatistics> findTop5BySelectionDateBetweenOrderBySalesQuantityDesc(LocalDate startDate, LocalDate endDate);

}


