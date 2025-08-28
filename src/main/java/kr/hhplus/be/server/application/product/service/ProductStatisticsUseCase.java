package kr.hhplus.be.server.application.product.service;

import kr.hhplus.be.server.application.product.dto.ProductResponse;

import java.util.List;

public interface ProductStatisticsUseCase {

    /**
     * 지난 3일간 가장 많이 팔린 TOP5 상품 조회
     * @return List<ProductStatistics>
     */
    List<ProductResponse.Statistics> selectTop5SalesProductBySpecificRange();

    /**
     * 지난 3일간 판매 TOP 5 순위를 계산
     *
     * @return 합산된 판매량 순위 목록 (점수 포함)
     */
    List<ProductResponse.Statistics> getTop5ForLast3Days();
}
