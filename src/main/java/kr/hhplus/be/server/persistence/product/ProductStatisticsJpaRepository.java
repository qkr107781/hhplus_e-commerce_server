package kr.hhplus.be.server.persistence.product;

import kr.hhplus.be.server.domain.product.ProductStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProductStatisticsJpaRepository extends JpaRepository<ProductStatistics,Long> {

    List<ProductStatistics> findTop5BySelectionDateBetweenOrderBySalesQuantityDesc(LocalDate startDate, LocalDate endDate);

}


