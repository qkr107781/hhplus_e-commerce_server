package kr.hhplus.be.server.persistence.product;

import kr.hhplus.be.server.application.product.repository.ProductRepository;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductOption;

import java.util.List;

public class ProductAdapter implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;

    public ProductAdapter(ProductJpaRepository productJpaRepository) {
        this.productJpaRepository = productJpaRepository;
    }

    @Override
    public List<Product> findAllBySalseYn(String salseYn) {
        return productJpaRepository.findAllBySalseYn(salseYn);
    }

    @Override
    public List<ProductOption> findByProductOptionIds(List<Long> productOptionIds) {
        return productJpaRepository.findByProductOptionIds(productOptionIds);
    }

    @Override
    public ProductOption updateStockQuantity(ProductOption productOption) {
        return productJpaRepository.updateStockQuantity(productOption);
    }

    @Override
    public Product findByProductId(long productId) {
        return productJpaRepository.findByProductId(productId);
    }

    @Override
    public ProductOption findByProductIdAndProductOptionId(long productId, long productOptionId) {
        return productJpaRepository.findByProductIdAndProductOptionId(productId,productOptionId);
    }
}
