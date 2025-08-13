package kr.hhplus.be.server.application.product.repository;

import kr.hhplus.be.server.domain.product.ProductOption;
import java.util.List;

public interface ProductOptionRepository {

    ProductOption selectProductOptionByProductOptionId(Long productOptionId);

    List<ProductOption> selectProductOptionByProductOptionIdInWithLock(List<Long> productOptionIds);

    ProductOption save(ProductOption productOption);

    ProductOption selectProductOptionByProductIdAndProductOptionId(long productId,long productOptionId);

    List<ProductOption> selectProductOptionByProductIdAndSalesYn(long productId,String salesYn);
}