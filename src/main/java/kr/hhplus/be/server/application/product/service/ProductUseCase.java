package kr.hhplus.be.server.application.product.service;

import kr.hhplus.be.server.application.product.dto.ProductResponse;

import java.util.List;

public interface ProductUseCase {

    List<ProductResponse.Select> selectSalesProductList();

}
