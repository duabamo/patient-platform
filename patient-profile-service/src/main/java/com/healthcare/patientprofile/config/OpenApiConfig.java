package com.healthcare.patientprofile.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI patientProfileOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Patient Profile Service API")
                        .description("API de gestion des donnees demographiques, pathologies et traitements pour la plateforme de suivi des patients chroniques.")
                        .version("1.0.0")
                        .contact(new Contact().name("Healthcare Platform Team").email("contact@healthcare.local"))
                        .license(new License().name("Internal").url("https://healthcare.local")));
    }
}
