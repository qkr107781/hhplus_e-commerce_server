package kr.hhplus.be.server.application.product.repository;

import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductStatistics;

import java.time.LocalDate;
import java.util.List;

public interface ProductRepository {

    List<Product> selectAllProduct();

    Product selectProductByProductId(long productId);

    List<ProductStatistics> findTop5BySelectionDateBetweenOrderBySalesQuantityDesc(LocalDate startDate, LocalDate endDate);
}