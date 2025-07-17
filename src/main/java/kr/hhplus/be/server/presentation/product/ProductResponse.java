package kr.hhplus.be.server.presentation.product;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProductResponse {

    public record Option(
            long product_option_id,
            String option_name,
            long price,
            long total_quantity,
            long stock_quantity,
            String sales_yn,
            LocalDateTime reg_date
    ) {
    }

    public record Select(
            long product_id,
            String name,
            String description,
            List<ProductResponse.Option> options
    ) {
        public static List<Select> from(List<Select> products) {
            return new ArrayList<>(products);
        }
    }

    public record Statistics(
        long product_id,
        long product_option_id,
        String product_name,
        String product_option_name,
        long price,
        long sales_quantity ,
        long ranking,
        LocalDateTime selection_date
    ){
        public static List<Statistics> from(List<Statistics> statistics) {
            return new ArrayList<>(statistics);
        }
    }
}
