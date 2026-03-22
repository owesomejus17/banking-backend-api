package com.securebank.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures the Swagger/OpenAPI 3 documentation.
 * Swagger UI is accessible at: http://localhost:8080/swagger-ui.html
 *
 * @SecurityScheme tells Swagger UI to show a "Authorize" button that
 * lets you paste your JWT and automatically include it in all test requests.
 */
@Configuration
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Paste your JWT token (without 'Bearer' prefix) to authorize all secured endpoints"
)
public class OpenApiConfig {

    @Bean
    public OpenAPI secureBankOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SecureBank API")
                        .version("1.0.0")
                        .description("""
                                A production-quality banking REST API demonstrating:
                                - JWT Authentication (Spring Security 6)
                                - Account management (SAVINGS / CURRENT)
                                - Atomic transactions (deposit, withdrawal, transfer)
                                - Paginated transaction history
                                - Comprehensive error handling
                                """)
                        .contact(new Contact()
                                .name("SecureBank Team")
                                .email("support@securebank.com")));
    }
}
