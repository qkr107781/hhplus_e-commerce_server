package kr.hhplus.be.server.persistence.product;

import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductOption;
import kr.hhplus.be.server.domain.product.ProductStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProductJpaRepository extends JpaRepository<Product,Long> {

    List<Product> findByProductOptions_SalesYn(String salseYn);

    List<ProductOption> findByProductOptionsIn(List<Long> productOptionIds);

    ProductOption save(ProductOption productOption);

    Product findByProductId(long productId);

    ProductOption findByProductIdAndProductOptions_ProductOptionId(long productId,long productOptionId);

    // JPQL을 사용하여 쿼리 작성 (정확성과 유연성 확보)
    @Query("SELECT s FROM ProductStatistics s " +
            "WHERE s.selectionDate >= :startDate AND s.selectionDate < :endDate " + // 날짜 범위 조건
            "ORDER BY s.salesQuantity DESC") // salesQuantity 내림차순 정렬
    List<ProductStatistics> findTop5BySelectionDateRangeOrderBySalesQuantityDesc(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );


}
