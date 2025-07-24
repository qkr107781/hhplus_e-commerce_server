package kr.hhplus.be.server.application.product.service;

import kr.hhplus.be.server.application.product.dto.ProductResponse;
import kr.hhplus.be.server.application.product.repository.ProductRepository;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductOption;
import kr.hhplus.be.server.domain.product.ProductStatistics;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService implements ProductUseCase {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * 상품 목록 조회
     *
     * @return List<ProductResponse.Select>
     */
    @Override
    public List<ProductResponse.Select> selectSalesProductList() {
        List<Product> salesProductList = productRepository.findByProductOptions_SalesYn("Y");
        return ProductResponse.Select.from(salesProductList);
    }

    /**
     * 재고 차감
     *
     * @param requestProductOptionIds: 상품 옵션 ID 목록
     * @return List<ProductOption>
     * @throws Exception
     */
    public List<ProductOption> decreaseStock(List<Long> requestProductOptionIds) throws Exception {
        //상품 잔여 갯수 확인
        List<ProductOption> productOptionListForDecreaseStock = new ArrayList<>();
        List<ProductOption> productOptionList = productRepository.findByProductOptionsIn(requestProductOptionIds);
        for (ProductOption productOption : productOptionList) {
            if (productOption.getStockQuantity() == 0) {
                throw new Exception("stock empty");
            } else {
                //재고 차감을 위해 List에 할당
                productOptionListForDecreaseStock.add(productOption);
            }
        }

        //재고 차감
        for (ProductOption productOption : productOptionListForDecreaseStock) {
            productOption.decreaseProductQuantity();
            productRepository.save(productOption);
        }

        return productOptionListForDecreaseStock;
    }

    /**
     * 총 주문 금액 계산
     *
     * @param productOptionList: 상품 옵션 목록
     * @return totalOrderPrice
     */
    public long calculateProductTotalPrice(List<ProductOption> productOptionList) {
        long totalOrderPrice = 0L;
        if (!productOptionList.isEmpty()) {
            for (ProductOption productOption : productOptionList) {
                //총 주문 금액 산정
                totalOrderPrice += productOption.getPrice();
            }
        }
        return totalOrderPrice;
    }

    /**
     * 상품 조회
     *
     * @param requestProductId: 상품 ID
     * @return
     */
    public Product selectProductByProductId(long requestProductId) {
        return productRepository.findByProductId(requestProductId);
    }

    /**
     * 상품 옵션 조회
     *
     * @param productId:       상품 ID
     * @param productOptionId: 상품 옵션 ID
     * @return ProductOption
     */
    public ProductOption selectProductOptionByProductIdAndProductOptionId(long productId, long productOptionId) {
        return productRepository.findByProductIdAndProductOptions_ProductOptionId(productId, productOptionId);
    }

    /**
     * 오늘 기준 4일 전부터 1일 전까지의 데이터 중 salesQuantity 기준 상위 5개를 조회합니다.
     * 예: 오늘이 7월 25일이면, 7월 21일 00:00:00 부터 7월 24일 23:59:59.999999999까지의 데이터를 조회합니다.
     * @return 상위 5개 통계 데이터 리스트
     */
    @Override
    public List<ProductStatistics> selectTop5SalesStatisticsSpecificRange() {
        LocalDate today = LocalDate.now(); // 2025-07-25

        // 시작 날짜: 오늘 기준 4일 전의 00:00:00
        // 예: 2025-07-25 - 4일 = 2025-07-21, 따라서 2025-07-21 00:00:00
        LocalDateTime startDate = today.minusDays(4).atStartOfDay();

        // 종료 날짜: 오늘 기준 1일 전의 23:59:59.999999999
        // 예: 2025-07-25 - 1일 = 2025-07-24, 따라서 2025-07-24 23:59:59.999999999
        // `minusDays(1).atStartOfDay().plusDays(1).minusNanos(1)`로 1일 전의 "끝"까지를 표현합니다.
        // 또는 단순히 `today.minusDays(1).atStartOfDay()`를 endDate로 사용하고, 쿼리에서 `< :endDate` 대신 `<= :endDate`를 고려할 수도 있습니다.
        // 여기서는 `startDate`는 포함, `endDate`는 미만을 사용하므로 다음과 같이 정확히 설정합니다.
        LocalDateTime endDate = today.minusDays(1).atStartOfDay().plusDays(1).minusNanos(1);

        // 이제 위에서 정의한 쿼리 메서드를 호출합니다.
        return productRepository.findTop5BySelectionDateRangeOrderBySalesQuantityDesc(startDate, endDate);
    }
}