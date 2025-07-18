package kr.hhplus.be.server.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("E-Commerce API")
                        .version("1.0..0")
                        .description("- e-커머스 상품 주문 서비스로 잔액 충전/조회, 상품 조회, 선착순 쿠폰 발급/조회, 주문, 결제, 인기 상품 조회를 제공합니다.\n" +
                                "- 사용자는 여러 상품을 조회/주문하고 발급받은 쿠폰을 사용하여 할인된 금액에 대해 미리 충전한 잔액으로 결제 합니다.\n" +
                                "- 주문 내역을 기반으로 지난 3일간 가장 많이 팔린 TOP5 상품 정보를 제공합니다."));
    }
}