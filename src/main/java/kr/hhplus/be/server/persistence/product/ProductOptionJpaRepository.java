package kr.hhplus.be.server.persistence.product;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.product.ProductOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductOptionJpaRepository extends JpaRepository<ProductOption,Long> {

    ProductOption findByProductOptionId(Long productOptionId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    ProductOption findById(long productOptionId);

    List<ProductOption> findByProductIdAndSalesYn(long productId,String salesYn);

    ProductOption findByProductIdAndProductOptionId(long productId,long productOptionId);

}
