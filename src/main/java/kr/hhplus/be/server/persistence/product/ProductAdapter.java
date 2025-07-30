package kr.hhplus.be.server.persistence.product;

import kr.hhplus.be.server.application.product.repository.ProductRepository;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductStatistics;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class ProductAdapter implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;
    private final ProductStatisticsJpaRepository productStatisticsJpaRepository;

    public ProductAdapter(ProductJpaRepository productJpaRepository, ProductStatisticsJpaRepository productStatisticsJpaRepository) {
        this.productJpaRepository = productJpaRepository;
        this.productStatisticsJpaRepository = productStatisticsJpaRepository;
    }

    @Override
    public List<Product> selectAllProduct() {
        return productJpaRepository.findAll();
    }

    @Override
    public Product selectProductByProductId(long productId) {
        return productJpaRepository.findByProductId(productId);
    }

    @Override
    public List<ProductStatistics> findTop5BySelectionDateBetweenOrderBySalesQuantityDesc(LocalDate startDate, LocalDate endDate) {
        return productStatisticsJpaRepository.findTop5BySelectionDateBetweenOrderBySalesQuantityDesc(startDate,endDate);
    }


}
