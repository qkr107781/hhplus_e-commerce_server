package kr.hhplus.be.server.persistence.product;

import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductJpaRepository extends JpaRepository<Product,Long> {

    List<Product> findAllBySalseYn(String salseYn);

    List<ProductOption> findByProductOptionIds(List<Long> productOptionIds);

    ProductOption updateStockQuantity(ProductOption productOption);

    Product findByProductId(long productId);

    ProductOption findByProductIdAndProductOptionId(long productId,long productOptionId);

}
