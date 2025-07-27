package kr.hhplus.be.server.presentation.product;

import kr.hhplus.be.server.application.product.dto.ProductResponse;
import kr.hhplus.be.server.swagger.ProductApiSpec;
import kr.hhplus.be.server.util.DummyDataUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/")
public class ProductController implements ProductApiSpec {

    private final DummyDataUtil dummyDataUtil;

    public ProductController(DummyDataUtil dummyDataUtil) {
        this.dummyDataUtil = dummyDataUtil;
    }

    @GetMapping("/products")
    @Override
    public ResponseEntity<List<ProductResponse.Select>> productSelect(){
        return ResponseEntity.ok(dummyDataUtil.getProductsSelect());
    }

    @GetMapping("/products/statistics")
    @Override
    public ResponseEntity<List<ProductResponse.Statistics>> productStatistics(){
        return ResponseEntity.ok(dummyDataUtil.getProductsStatistics());
    }
}