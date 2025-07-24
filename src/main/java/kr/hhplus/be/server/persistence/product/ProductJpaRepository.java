package kr.hhplus.be.server.persistence.product;

import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductJpaRepository extends JpaRepository<Product,Long> {

    List<Product> findByProductOptions_SalesYn(String salseYn);

    List<ProductOption> findByProductOptionsIn(List<Long> productOptionIds);

    ProductOption save(ProductOption productOption);

    Product findByProductId(long productId);

    ProductOption findByProductIdAndProductOptions_ProductOptionId(long productId,long productOptionId);

}
