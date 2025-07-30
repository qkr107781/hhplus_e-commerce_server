package kr.hhplus.be.server.application.product.repository;

import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductOption;
import kr.hhplus.be.server.domain.product.ProductStatistics;

import java.time.LocalDate;
import java.util.List;

public interface ProductRepository {

    List<Product> findByProductOptions_SalesYn(String salseYn);

    List<ProductOption> findByProductOptionsIn(List<Long> productOptionIds);

    ProductOption save(ProductOption productOption);

    Product findByProductId(long productId);

    ProductOption findByProductIdAndProductOptions_ProductOptionId(long productId,long productOptionId);

    List<ProductStatistics> findTop5BySelectionDateBetweenOrderBySalesQuantityDesc(LocalDate startDate, LocalDate endDate);
}