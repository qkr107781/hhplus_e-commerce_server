package kr.hhplus.be.server.application.product.service;

import kr.hhplus.be.server.application.product.dto.ProductResponse;
import kr.hhplus.be.server.application.product.repository.ProductOptionRepository;
import kr.hhplus.be.server.application.product.repository.ProductRepository;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductOption;
import org.springframework.stereotype.Service;

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
     * 재고 차감/복구 전 Lock 획득을 위한 조회
     * @param productOptionIds: 상품 옵션 ID 목록
     * @return List<ProductOption>
     */
    public List<ProductOption> selectProductOptionByProductOptionIdInWithLock(List<Long> productOptionIds){
        return productOptionRepository.selectProductOptionByProductOptionIdInWithLock(productOptionIds);
    }

    /**
     * 재고 차감/복구 전 조회
     * @param productOptionIds: 상품 옵션 ID 목록
     * @return List<ProductOption>
     */
    public List<ProductOption> selectProductOptionByProductOptionIdIn(List<Long> productOptionIds){
        return productOptionRepository.selectProductOptionByProductOptionIdIn(productOptionIds);
    }

    /**
     * 재고 차감
     * @param productOption: 상품 옵션 정보
     * @param quantityToDecrease: 차감 수량
     * @return ProductOption
     * @throws Exception
     */
    public ProductOption decreaseStock(ProductOption productOption, long quantityToDecrease) throws Exception {
        System.out.println(productOption.getProductOptionId()+" 재고 차감 전: "+productOption.getStockQuantity());
        //재고 차감
        productOption.decreaseProductQuantity(quantityToDecrease);
        System.out.println(productOption.getProductOptionId()+" 재고 차감 후: "+productOption.getStockQuantity());
        return productOptionRepository.save(productOption);
    }

    /**
     * 재고 복구
     * @param productOption: 상품 옵션 정보
     * @param quantityToRestore: 복구 수량
     * @return ProductOption
     * @throws Exception
     */
    public ProductOption restoreStock(ProductOption productOption, long quantityToRestore) throws Exception {
        System.out.println(productOption.getProductOptionId()+" 재고 복구 전: "+productOption.getStockQuantity());
        //재고 복구
        productOption.restoreProductQuantity(quantityToRestore);
        System.out.println(productOption.getProductOptionId()+" 재고 복구 후: "+productOption.getStockQuantity());

        return productOptionRepository.save(productOption);
    }

    /**
     * 총 주문 금액 계산
     *
     * @param productOption: 상품 옵션 목록
     * @return totalOrderPrice
     */
    public long calculateProductTotalPrice(ProductOption productOption, long quantityToDecrease) throws Exception {
        long totalOrderPrice = 0L;
        if (productOption != null) {
            //총 주문 금액 산정
            totalOrderPrice += productOption.getPrice() * quantityToDecrease;
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
     * @return Product
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
     * 상품 옵션 조회
     *
     * @param productOptionId: 상품 옵션 ID
     * @return ProductOption
     */
    public ProductOption selectProductOptionByProductOptionId(long productOptionId) {
        return productOptionRepository.selectProductOptionByProductOptionId(productOptionId);
    }

}