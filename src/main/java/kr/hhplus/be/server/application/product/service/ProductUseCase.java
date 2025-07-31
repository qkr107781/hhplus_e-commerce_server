package kr.hhplus.be.server.application.product.service;

import kr.hhplus.be.server.application.product.dto.ProductResponse;

import java.util.List;

public interface ProductUseCase {

    /**
     * 상품 목록 조회
     * @return List<ProductResponse.Select>
     */
    List<ProductResponse.Select> selectSalesProductList();

}
