package kr.hhplus.be.server.persistence.product;

import kr.hhplus.be.server.domain.product.ProductOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductOptionJpaRepository extends JpaRepository<ProductOption,Long> {

    ProductOption findByProductOptionId(Long productOptionId);

    List<ProductOption> findByProductIdAndSalesYn(long productId,String salesYn);

    ProductOption findByProductIdAndProductOptionId(long productId,long productOptionId);

}
