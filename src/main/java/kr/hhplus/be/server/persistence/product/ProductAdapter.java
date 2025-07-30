package kr.hhplus.be.server.persistence.product;

import kr.hhplus.be.server.application.product.repository.ProductRepository;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductOption;
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
    public List<Product> findByProductOptions_SalesYn(String salseYn) {
        return productJpaRepository.findByProductOptions_SalesYn(salseYn);
    }

    @Override
    public List<ProductOption> findByProductOptionsIn(List<Long> productOptionIds) {
        return productJpaRepository.findByProductOptionsIn(productOptionIds);
    }

    @Override
    public ProductOption save(ProductOption productOption) {
        return productJpaRepository.save(productOption);
    }

    @Override
    public Product findByProductId(long productId) {
        return productJpaRepository.findByProductId(productId);
    }

    @Override
    public ProductOption findByProductIdAndProductOptions_ProductOptionId(long productId, long productOptionId) {
        return productJpaRepository.findByProductIdAndProductOptions_ProductOptionId(productId,productOptionId);
    }

    @Override
    public List<ProductStatistics> findTop5BySelectionDateBetweenOrderBySalesQuantityDesc(LocalDate startDate, LocalDate endDate) {
        return productStatisticsJpaRepository.findTop5BySelectionDateBetweenOrderBySalesQuantityDesc(startDate,endDate);
    }


}
