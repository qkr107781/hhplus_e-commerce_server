package kr.hhplus.be.server.presentation.product;

import kr.hhplus.be.server.application.product.dto.ProductResponse;
import kr.hhplus.be.server.application.product.service.ProductStatisticsUseCase;
import kr.hhplus.be.server.application.product.service.ProductUseCase;
import kr.hhplus.be.server.swagger.ProductApiSpec;
import kr.hhplus.be.server.util.DummyDataUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/")
public class ProductController implements ProductApiSpec {

    private final ProductUseCase productUseCase;
    private final ProductStatisticsUseCase productStatisticsUseCase;
    private final DummyDataUtil dummyDataUtil;

    public ProductController(ProductUseCase productUseCase, ProductStatisticsUseCase productStatisticsUseCase, DummyDataUtil dummyDataUtil) {
        this.productUseCase = productUseCase;
        this.productStatisticsUseCase = productStatisticsUseCase;
        this.dummyDataUtil = dummyDataUtil;
    }

    @GetMapping("/products")
    @Override
    public ResponseEntity<List<ProductResponse.Select>> productSelect(){
//        return ResponseEntity.ok(dummyDataUtil.getProductsSelect());
        return ResponseEntity.ok(productUseCase.selectSalesProductList());
    }

    @GetMapping("/products/statistics")
    @Override
    public ResponseEntity<List<ProductResponse.Statistics>> productStatistics(){
//        return ResponseEntity.ok(dummyDataUtil.getProductsStatistics());
        return ResponseEntity.ok(productStatisticsUseCase.selectTop5SalesProductBySpecificRange());
    }

}