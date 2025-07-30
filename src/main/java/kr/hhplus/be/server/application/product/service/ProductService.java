package kr.hhplus.be.server.application.product.service;

import kr.hhplus.be.server.application.product.dto.ProductResponse;
import kr.hhplus.be.server.application.product.repository.ProductOptionRepository;
import kr.hhplus.be.server.application.product.repository.ProductRepository;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductOption;
import kr.hhplus.be.server.domain.product.ProductStatistics;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService implements ProductUseCase {

    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;

    public ProductService(ProductRepository productRepository, ProductOptionRepository productOptionRepository) {
        this.productRepository = productRepository;
        this.productOptionRepository = productOptionRepository;
    }

    /**
     * 상품 목록 조회
     *
     * @return List<ProductResponse.Select>
     */
    @Override
    public List<ProductResponse.Select> selectSalesProductList() {
        List<Product> salesProductList = productRepository.selectAllProduct();
        List<ProductOption> salesProductOptionList = new ArrayList<>();
        for(Product product : salesProductList){
            List<ProductOption> selectProductOptionList = productOptionRepository.selectProductOptionByProductIdAndSalesYn(product.getProductId(),"Y");
            salesProductOptionList.addAll(selectProductOptionList);
        }

        return ProductResponse.Select.from(salesProductList,salesProductOptionList);
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
        List<ProductOption> productOptionList = productOptionRepository.selectProductOptionListByProductOptionIds(requestProductOptionIds);
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
            productOptionRepository.save(productOption);
        }

        return productOptionListForDecreaseStock;
    }

    /**
     * 총 주문 금액 계산
     *
     * @param productOptionList: 상품 옵션 목록
     * @return totalOrderPrice
     */
    public long calculateProductTotalPrice(List<ProductOption> productOptionList) throws Exception {
        long totalOrderPrice = 0L;
        if (!productOptionList.isEmpty()) {
            for (ProductOption productOption : productOptionList) {
                //총 주문 금액 산정
                totalOrderPrice += productOption.getPrice();
            }
        }
        if(totalOrderPrice == 0L){
            throw new Exception("empty total order price");
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
        return productRepository.selectProductByProductId(requestProductId);
    }

    /**
     * 상품 옵션 조회
     *
     * @param productId:       상품 ID
     * @param productOptionId: 상품 옵션 ID
     * @return ProductOption
     */
    public ProductOption selectProductOptionByProductIdAndProductOptionId(long productId, long productOptionId) {
        return productOptionRepository.selectProductOptionByProductIdAndProductOptionId(productId, productOptionId);
    }

    /**
     * 오늘 기준 4일 전부터 1일 전까지의 데이터 중 salesQuantity 기준 상위 5개를 조회합니다.
     * 예: 오늘이 7월 25일이면, 7월 21일 부터 7월 24일 까지의 데이터를 조회합니다.
     * @return 상위 5개 통계 데이터 리스트
     */
    @Override
    public List<ProductResponse.Statistics> selectTop5SalesStatisticsSpecificRange() {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(4);

        List<ProductStatistics> resultList = productRepository.findTop5BySelectionDateBetweenOrderBySalesQuantityDesc(startDate, endDate);

        return ProductResponse.Statistics.from(resultList);
    }
}