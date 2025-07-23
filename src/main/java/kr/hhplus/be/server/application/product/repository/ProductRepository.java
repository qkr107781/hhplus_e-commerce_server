package kr.hhplus.be.server.application.product.repository;

import kr.hhplus.be.server.domain.product.Product;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository {

    List<Product> findAllBySalseYn(String salseYn);

}