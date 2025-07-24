package kr.hhplus.be.server.application.product.service;

import kr.hhplus.be.server.application.product.dto.ProductResponse;
import kr.hhplus.be.server.application.product.repository.ProductRepository;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductOption;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService implements ProductUseCase{

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * 상품 목록 조회
     * @return List<ProductResponse.Select>
     */
    @Override
    public List<ProductResponse.Select> selectSalesProductList() {
        List<Product> salesProductList = productRepository.findAllBySalseYn("Y");
        return ProductResponse.Select.from(salesProductList);
    }

    /**
     * 재고 차감
     * @param requestProductOptionIds: 상품 옵션 ID 목록
     * @return List<ProductOption>
     * @throws Exception
     */
    public List<ProductOption> decreaseStock(List<Long> requestProductOptionIds) throws Exception {
        //상품 잔여 갯수 확인
        List<ProductOption> productOptionListForDecreaseStock = new ArrayList<>();
        List<ProductOption> productOptionList = productRepository.findByProductOptionIds(requestProductOptionIds);
        for(ProductOption productOption : productOptionList){
            if(productOption.getStockQuantity() == 0){
                throw new Exception("stock empty");
            }else{
                //재고 차감을 위해 List에 할당
                productOptionListForDecreaseStock.add(productOption);
            }
        }

        //재고 차감
        for(ProductOption productOption : productOptionListForDecreaseStock){
            productOption.decreaseProductQuantity();
            productRepository.updateStockQuantity(productOption);
        }

        return productOptionListForDecreaseStock;
    }

    /**
     * 총 주문 금액 계산
     * @param productOptionList: 상품 옵션 목록
     * @return totalOrderPrice
     */
    public long calculateProductTotalPrice(List<ProductOption> productOptionList){
        long totalOrderPrice = 0L;
        if(!productOptionList.isEmpty()){
            for(ProductOption productOption : productOptionList){
                //총 주문 금액 산정
                totalOrderPrice += productOption.getPrice();
            }
        }
        return totalOrderPrice;
    }

    /**
     * 상품 조회
     * @param requestProductId: 상품 ID
     * @return
     */
    public Product selectProductByProductId(long requestProductId){
        return productRepository.findByProductId(requestProductId);
    }

    /**
     * 상품 옵션 조회
     * @param productId: 상품 ID
     * @param productOptionId: 상품 옵션 ID
     * @return ProductOption
     */
    public ProductOption selectProductOptionByProductIdAndProductOptionId(long productId,long productOptionId){
        return productRepository.findByProductIdAndProductOptionId(productId,productOptionId);
    }
}
