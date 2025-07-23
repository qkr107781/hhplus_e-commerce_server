package kr.hhplus.be.server.persistence.product;

import kr.hhplus.be.server.application.product.repository.ProductRepository;
import kr.hhplus.be.server.domain.product.Product;

import java.util.List;

public class ProductRepositoryImpl implements ProductRepository {

    @Override
    public List<Product> findAllBySalseYn(String salseYn) {
        return List.of();
    }
}
