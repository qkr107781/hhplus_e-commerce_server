package kr.hhplus.be.server.application.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductStatistics;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ProductResponse {

    public record Option(
            @Schema(description = "상품 옵션 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            long productOptionId,
            @Schema(description = "상품 옵션 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            String optionName,
            @Schema(description = "단가", requiredMode = Schema.RequiredMode.REQUIRED)
            long price,
            @Schema(description = "판매 여부", requiredMode = Schema.RequiredMode.REQUIRED)
            String salesYn,
            @Schema(description = "등록일", requiredMode = Schema.RequiredMode.REQUIRED)
            LocalDateTime regDate
    ) {
    }

    public record Select(
            @Schema(description = "상품 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            long productId,
            @Schema(description = "상품명", requiredMode = Schema.RequiredMode.REQUIRED)
            String name,
            @Schema(description = "상품 옵션 목록", requiredMode = Schema.RequiredMode.REQUIRED)
            List<ProductResponse.Option> options
    ) {
        public static List<ProductResponse.Select> from(List<Product> products) {
            return products.stream()
                    .map(product -> new ProductResponse.Select(product.getProductId(),
                                                                        product.getName(),
                                                                        product.getProductOptions().stream()
                                                                                                    .map(productOption -> new ProductResponse.Option(
                                                                                                                productOption.getProductOptionId(),
                                                                                                                productOption.getOptionName(),
                                                                                                                productOption.getPrice(),
                                                                                                                productOption.getSalesYn(),
                                                                                                                productOption.getRegDate())).collect(Collectors.toList())))
                    .collect(Collectors.toList());
        }
    }

    public record Statistics(
            @Schema(description = "상품 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            long productId,
            @Schema(description = "상품명", requiredMode = Schema.RequiredMode.REQUIRED)
            String productName,
            @Schema(description = "판매 수량", requiredMode = Schema.RequiredMode.REQUIRED)
            long salesQuantity ,
            @Schema(description = "선정일", requiredMode = Schema.RequiredMode.REQUIRED)
            LocalDate selectionDate
    ){
        public static List<ProductResponse.Statistics> from(List<ProductStatistics> statistics) {
            return statistics.stream().map(ps -> new ProductResponse.Statistics(
                    ps.getProductId(),
                    ps.getProductName(),
                    ps.getSalesQuantity(),
                    ps.getSelectionDate()
            )).toList();
        }
    }
}