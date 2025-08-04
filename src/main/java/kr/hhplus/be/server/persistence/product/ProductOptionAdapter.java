package kr.hhplus.be.server.persistence.product;

import kr.hhplus.be.server.application.product.repository.ProductOptionRepository;
import kr.hhplus.be.server.domain.product.ProductOption;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductOptionAdapter implements ProductOptionRepository {

    private final ProductOptionJpaRepository productOptionJpaRepository;

    public ProductOptionAdapter(ProductOptionJpaRepository productOptionJpaRepository) {
        this.productOptionJpaRepository = productOptionJpaRepository;
    }

    @Override
    public ProductOption selectProductOptionByProductOptionId(Long productOptionId) {
        return productOptionJpaRepository.findByProductOptionId(productOptionId);
    }

    @Override
    public ProductOption save(ProductOption productOption) {
        return productOptionJpaRepository.save(productOption);
    }

    @Override
    public ProductOption selectProductOptionByProductIdAndProductOptionId(long productId, long productOptionId) {
        return productOptionJpaRepository.findByProductIdAndProductOptionId(productId,productOptionId);
    }

    @Override
    public List<ProductOption> selectProductOptionByProductIdAndSalesYn(long productId,String salesYn) {
        return productOptionJpaRepository.findByProductIdAndSalesYn(productId,salesYn);
    }

}
