package kr.hhplus.be.server.application.product.dto;

import com.querydsl.core.Tuple;
import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.domain.order.QOrderProduct;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductOption;
import kr.hhplus.be.server.domain.product.QProductOption;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProductResponse {

    public record Option(
            @Schema(description = "상품 옵션 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            long productOptionId,
            @Schema(description = "상품 옵션명", requiredMode = Schema.RequiredMode.REQUIRED) // description 수정
            String optionName,
            @Schema(description = "단가", requiredMode = Schema.RequiredMode.REQUIRED)
            long price,
            @Schema(description = "판매 여부", requiredMode = Schema.RequiredMode.REQUIRED)
            String salesYn,
            @Schema(description = "등록일", requiredMode = Schema.RequiredMode.REQUIRED)
            LocalDateTime regDate
    ) {
        // ProductOption 엔티티로부터 Option 레코드 생성하는 팩토리 메서드 추가
        public static Option from(ProductOption productOption) {
            return new Option(
                    productOption.getProductOptionId(),
                    productOption.getOptionName(),
                    productOption.getPrice(),
                    productOption.getSalesYn(),
                    productOption.getRegDate()
            );
        }
    }

    public record Select(
            @Schema(description = "상품 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            long productId,
            @Schema(description = "상품명", requiredMode = Schema.RequiredMode.REQUIRED)
            String name,
            @Schema(description = "상품 옵션 목록", requiredMode = Schema.RequiredMode.REQUIRED)
            List<ProductResponse.Option> options
    ) {
        // from 팩토리 메서드 시그니처 변경: List<ProductOption>이 "하나의 평평한 리스트"로 들어옴
        public static List<ProductResponse.Select> from(List<Product> products, List<ProductOption> allProductOptions) {
            // ProductId를 기준으로 ProductOption들을 그룹화 (Map<Long, List<ProductOption>>)
            Map<Long, List<ProductOption>> optionsByProductId = allProductOptions.stream()
                    // ProductOption 엔티티에 getProductId() 또는 getProduct().getProductId() 가 있어야 함
                    // 예시에서는 ProductOption 엔티티에 Product 객체 참조(ManyToOne)가 있다고 가정
                    .collect(Collectors.groupingBy(ProductOption::getProductId));

            return products.stream()
                    .map(product -> {
                        // 해당 Product의 ProductId에 맞는 옵션 리스트를 가져옴
                        List<ProductOption> associatedOptions = optionsByProductId.getOrDefault(
                                product.getProductId(),
                                List.of() // 해당 상품에 옵션이 없을 경우 빈 리스트 반환 (Java 9+ List.of())
                                // 또는 new ArrayList<>() for Java 8
                        );

                        // ProductOption 리스트를 ProductResponse.Option 레코드 리스트로 변환
                        List<ProductResponse.Option> optionDtos = associatedOptions.stream()
                                .map(ProductResponse.Option::from) // Option 레코드의 팩토리 메서드 사용
                                .collect(Collectors.toList());

                        return new ProductResponse.Select(
                                product.getProductId(),
                                product.getName(),
                                optionDtos
                        );
                    })
                    .collect(Collectors.toList());
        }
    }

    public record Statistics(
            @Schema(description = "상품명", requiredMode = Schema.RequiredMode.REQUIRED)
            String productName,
            @Schema(description = "판매 수량", requiredMode = Schema.RequiredMode.REQUIRED)
            Long salesQuantity
    ){
        public static List<ProductResponse.Statistics> from(List<Tuple> statistics) {
            QOrderProduct orderProduct = QOrderProduct.orderProduct;
            QProductOption productOption = QProductOption.productOption;
            return statistics.stream().map(ps -> new ProductResponse.Statistics(
                    ps.get(productOption.optionName),
                    ps.get(orderProduct.productQuantity.sum())
            )).toList();
        }
    }
}