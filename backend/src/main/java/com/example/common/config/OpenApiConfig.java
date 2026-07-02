package com.example.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI Configuration
 * Provides API documentation accessible at: http://localhost:8080/api/swagger-ui.html
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Fashion Outfit Suggestions API")
                        .version("1.0.0")
                        .description("""n
                                Complete API documentation for Fashion Outfit Suggestions Application.
                                
                                ## Key Features:
                                - 👤 User authentication (JWT + OAuth2 Google)
                                - 👕 Wardrobe management (items, outfits)
                                - 👥 Social features (friends, chat)
                                - 🔔 Real-time notifications
                                - 📸 Image uploads via Cloudinary
                                - 📱 Firebase push notifications
                                
                                ## Authentication:
                                All protected endpoints require JWT token in Authorization header:
                                ```
                                Authorization: Bearer <your-jwt-token>
                                ```
                                """)
                        .contact(new Contact()
                                .name("Fashion Outfit Team")
                                .url("https://github.com/nghiaMeo/fashion-outfit-suggestions-application")
                                .email("support@example.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT Authentication Token")
                                .name("Authorization")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
