package kr.hhplus.be.server.application.product.repository;

import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductOption;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository {

    List<Product> findAllBySalseYn(String salseYn);

    List<ProductOption> findByProductOptionIds(List<Long> productOptionIds);

    ProductOption updateStockQuantity(ProductOption productOption);

    Product findByProductId(long productId);

    ProductOption findByProductIdAndProductOptionId(long productId,long productOptionId);
}