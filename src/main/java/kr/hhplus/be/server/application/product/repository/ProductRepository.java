package kr.hhplus.be.server.application.product.repository;

import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductOption;
import kr.hhplus.be.server.domain.product.ProductStatistics;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductRepository {

    List<Product> findByProductOptions_SalesYn(String salseYn);

    List<ProductOption> findByProductOptionsIn(List<Long> productOptionIds);

    ProductOption save(ProductOption productOption);

    Product findByProductId(long productId);

    ProductOption findByProductIdAndProductOptions_ProductOptionId(long productId,long productOptionId);

    List<ProductStatistics> findTop5BySelectionDateRangeOrderBySalesQuantityDesc(LocalDateTime startDate,LocalDateTime endDate);
}