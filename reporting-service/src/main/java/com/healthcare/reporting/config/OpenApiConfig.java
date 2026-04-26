package com.healthcare.reporting.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI reportingOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("Reporting Service API")
                .description("API de generation de rapports hebdomadaires et mensuels pour la plateforme de suivi des patients chroniques.")
                .version("1.0.0"));
    }
}
