package kr.hhplus.be.server.persistence.product;

import kr.hhplus.be.server.application.product.repository.ProductRepository;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductOption;
import kr.hhplus.be.server.domain.product.ProductStatistics;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ProductAdapter implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;

    public ProductAdapter(ProductJpaRepository productJpaRepository) {
        this.productJpaRepository = productJpaRepository;
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
    public List<ProductStatistics> findTop5BySelectionDateRangeOrderBySalesQuantityDesc(LocalDateTime startDate, LocalDateTime endDate) {
        return productJpaRepository.findTop5BySelectionDateRangeOrderBySalesQuantityDesc(startDate,endDate);
    }
}
