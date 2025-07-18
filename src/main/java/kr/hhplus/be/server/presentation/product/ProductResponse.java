package kr.hhplus.be.server.presentation.product;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProductResponse {

    public record Option(
            @Schema(description = "상품 옵션 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            long productOptionId,
            @Schema(description = "상품 옵션 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            String optionName,
            @Schema(description = "단가", requiredMode = Schema.RequiredMode.REQUIRED)
            long price,
            @Schema(description = "총 수량", requiredMode = Schema.RequiredMode.REQUIRED)
            long totalQuantity,
            @Schema(description = "잔여 수량", requiredMode = Schema.RequiredMode.REQUIRED)
            long stockQuantity,
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
            @Schema(description = "설명", requiredMode = Schema.RequiredMode.REQUIRED)
            String description,
            @Schema(description = "상품 옵션 목록", requiredMode = Schema.RequiredMode.REQUIRED)
            List<ProductResponse.Option> options
    ) {
        public static List<Select> from(List<Select> products) {
            return new ArrayList<>(products);
        }
    }

    public record Statistics(
            @Schema(description = "상품 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            long productId,
            @Schema(description = "상품 옵션 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            long productOptionId,
            @Schema(description = "상품명", requiredMode = Schema.RequiredMode.REQUIRED)
            String productName,
            @Schema(description = "상품 옵션명", requiredMode = Schema.RequiredMode.REQUIRED)
            String productOptionName,
            @Schema(description = "단가", requiredMode = Schema.RequiredMode.REQUIRED)
            long price,
            @Schema(description = "판매 수량", requiredMode = Schema.RequiredMode.REQUIRED)
            long salesQuantity ,
            @Schema(description = "인기 상품 순위", requiredMode = Schema.RequiredMode.REQUIRED)
            long ranking,
            @Schema(description = "선정일", requiredMode = Schema.RequiredMode.REQUIRED)
            LocalDateTime selectionDate
    ){
        public static List<Statistics> from(List<Statistics> statistics) {
            return new ArrayList<>(statistics);
        }
    }
}