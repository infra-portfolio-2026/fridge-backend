package univ.example.my_own_refrigerator.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        String jwtSchemeName = "jwtAuth";
        // API 요청 헤더에 인증 방식을 추가합니다.
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtSchemeName);

        // SecurityScheme: 인증 방식에 대한 정보를 정의합니다.
        Components components = new Components()
                .addSecuritySchemes(jwtSchemeName, new SecurityScheme()
                        .name(jwtSchemeName)
                        .type(SecurityScheme.Type.HTTP) // 인증 타입: HTTP
                        .scheme("bearer") // 인증 스키마: Bearer
                        .bearerFormat("JWT")); // 토큰 포맷: JWT

        return new OpenAPI()
                .info(new Info()
                        .title("My Refrigerator API")
                        .description("나만의 냉장고 API 문서")
                        .version("1.0"))
                .addSecurityItem(securityRequirement)
                .components(components);
    }
}
