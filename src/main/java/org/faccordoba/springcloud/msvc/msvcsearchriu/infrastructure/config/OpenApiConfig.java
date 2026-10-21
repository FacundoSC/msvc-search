package org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Value("${springdoc.api-docs.title:Search Microservice API - RIU Hotels & Resorts}")
    private String title;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(title)
                        .version("1.0.0")
                        .description("API for Search Microservice API - RIU Hotels & Resorts")
                        .contact(new Contact()
                                .name("Facundo Cordoba")
                                .email("faccordoba@riu.com"))
                        .license( new License()
                                .name("Apache 2.0")
                                .url("https://springdoc.org")));
    }
}
