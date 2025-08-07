package kr.hhplus.be.server.application.product.service;

import kr.hhplus.be.server.application.product.dto.ProductResponse;
import kr.hhplus.be.server.application.product.repository.ProductOptionRepository;
import kr.hhplus.be.server.application.product.repository.ProductRepository;
import kr.hhplus.be.server.domain.order.OrderProduct;
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
     * 재고 차감
     *
     * @param requestProductOptionId: 상품 옵션 ID
     * @return ProductOption
     * @throws Exception: 재고 0으로 차감 실패
     */
    public ProductOption decreaseStock(long requestProductOptionId) throws Exception {
        ProductOption productOption = productOptionRepository.selectProductOptionByProductOptionIdWithLock(requestProductOptionId);
        System.out.println(productOption.getProductOptionId()+" 재고 차감 전: "+productOption.getStockQuantity());
        //재고 차감
        productOption.decreaseProductQuantity();

        System.out.println(productOption.getProductOptionId()+" 재고 차감 후: "+productOption.getStockQuantity());
        return productOptionRepository.save(productOption);
    }

    public List<ProductOption> restoreStock(List<OrderProduct> cancelOrderProduct) throws Exception {
        List<ProductOption> productOptionListForRestoreStock = new ArrayList<>();

        //상품 잔여 갯수 복구
        for(OrderProduct orderProduct : cancelOrderProduct){
            ProductOption productOption = productOptionRepository.selectProductOptionByProductOptionIdWithLock(orderProduct.getProductOptionId());
            System.out.println(productOption.getProductOptionId()+" 재고 복구 전: "+productOption.getStockQuantity());
            productOption.restoreProductQuantity(orderProduct.getProductQuantity());
            productOptionRepository.save(productOption);
            System.out.println(productOption.getProductOptionId()+" 재고 복구 후: "+productOption.getStockQuantity());
            productOptionListForRestoreStock.add(productOption);
        }

        return productOptionListForRestoreStock;
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

}