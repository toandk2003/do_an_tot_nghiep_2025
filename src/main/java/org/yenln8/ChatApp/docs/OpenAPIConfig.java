package org.yenln8.ChatApp.docs;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        // JWT Security Scheme
                        .addSecuritySchemes("bearer-jwt",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .in(SecurityScheme.In.HEADER)
                                        .name("Authorization"))

                        // Accept-Language Parameter Component (có thể tái sử dụng)
                        .addParameters("Accept-Language",
                                new HeaderParameter()
                                        .name("Accept-Language")
                                        .description("Client's preferred language (optional)")
                                        .required(false)  // Optional
                                        .schema(new StringSchema()
                                                .example("en")  // Giá trị mặc định
                                                ._default("en") // Default value
                                                ._enum(java.util.List.of("en", "vi")) // English and Vietnamese
                                        ))
                )
                .info(new Info()
                        .title("Chat Application API")
                        .version("1.0")
                        .description("API documentation for Chat Application with internationalization support"))
                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"));
    }

    // Thêm Accept-Language header cho TẤT CẢ operations
    @Bean
    public OperationCustomizer addAcceptLanguageHeader() {
        return (operation, handlerMethod) -> {
            // Tạo Accept-Language parameter
            Parameter acceptLanguageParam = new HeaderParameter()
                    .name("Accept-Language")
                    .description("Preferred language for response (optional)")
                    .required(false)
                    .schema(new StringSchema()
                            .example("en")
                            ._default("en")
                            ._enum(java.util.List.of("en", "vi")));

            // Thêm parameter vào operation
            operation.addParametersItem(acceptLanguageParam);

            return operation;
        };
    }
}