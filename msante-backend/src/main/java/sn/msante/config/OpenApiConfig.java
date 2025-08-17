package sn.msante.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI 3.1 configuration for M-Santé API documentation
 */
@Configuration
public class OpenApiConfig {
    
    @Value("${spring.application.name:msante-backend}")
    private String applicationName;
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("M-Santé API")
                        .version("1.0.0")
                        .description("Healthcare platform backend for Senegal and West Africa providing appointment booking, payment processing, and secure messaging for patients and healthcare professionals.")
                        .contact(new Contact()
                                .name("M-Santé Support")
                                .email("support@msante.sn")
                                .url("https://msante.sn"))
                        .license(new License()
                                .name("Proprietary")
                                .url("https://msante.sn/license")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Development server"),
                        new Server().url("https://api.msante.sn").description("Production server")))
                .addSecurityItem(new SecurityRequirement().addList("bearer-key"))
                .components(new Components()
                        .addSecuritySchemes("bearer-key",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT token obtained from OTP verification")));
    }
}