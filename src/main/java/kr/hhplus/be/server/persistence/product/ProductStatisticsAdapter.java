package kr.hhplus.be.server.persistence.product;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.hhplus.be.server.application.order.dto.OrderProductSummary;
import kr.hhplus.be.server.application.product.repository.ProductStatisticsRepository;
import kr.hhplus.be.server.domain.order.QOrderProduct;
import kr.hhplus.be.server.domain.product.ProductStatistics;
import kr.hhplus.be.server.domain.product.QProductOption;
import kr.hhplus.be.server.domain.product.QProductStatistics;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductStatisticsAdapter implements ProductStatisticsRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public ProductStatisticsAdapter(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public List<ProductStatistics> selectTop5SalseProductByLast3Days(List<OrderProductSummary> orderProductList) {
        if (orderProductList == null || orderProductList.isEmpty()) {
            return List.of();
        }

        // OrderProduct 리스트의 ID들을 추출 (OrderProduct의 PK 또는 특정 식별자)
        List<Long> orderProductOptionIds = orderProductList.stream()
                .map(OrderProductSummary::productOptionId) // OrderProduct 엔티티의 ID (PK)라고 가정
                .collect(Collectors.toList());

        QOrderProduct orderProduct = QOrderProduct.orderProduct;
        QProductOption productOption = QProductOption.productOption;
        QProductStatistics productStatistics = QProductStatistics.productStatistics;

        return jpaQueryFactory
                .select(productStatistics) // 또는 new QProductOptionDto(...) 로 DTO 선택
                .from(orderProduct) // OrderProduct에서 시작
                .join(productOption).on(orderProduct.productOptionId.eq(productOption.productOptionId)) // productOptionId 기준으로 조인
                .where(orderProduct.productOptionId.in(orderProductOptionIds))
                .fetch();
    }
}
