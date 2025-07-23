package kr.hhplus.be.server.application.product.service;

import kr.hhplus.be.server.application.product.dto.ProductResponse;
import kr.hhplus.be.server.application.product.repository.ProductRepository;
import kr.hhplus.be.server.domain.product.Product;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService implements ProductUseCase{

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<ProductResponse.Select> selectSalesProductList() {
        List<Product> salesProductList = productRepository.findAllBySalseYn("Y");
        return ProductResponse.Select.from(salesProductList);
    }
}
