package kr.hhplus.be.server.application.product.repository;

import kr.hhplus.be.server.domain.product.Product;
import java.util.List;

public interface ProductRepository {

    List<Product> selectAllProduct();

    Product selectProductByProductId(long productId);
}