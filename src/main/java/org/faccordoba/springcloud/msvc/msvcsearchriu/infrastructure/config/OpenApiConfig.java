package org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class OpenApiConfig {
    private final String title;
    private final String version;
    private final String description;
    private final String name;

    public OpenApiConfig(@Value("${api.info.title}") String title,
                         @Value("${api.info.version}") String version,
                         @Value("${api.info.description}") String description,
                         @Value("${api.info.name}") String name) {
        this.title = title;
        this.version = version;
        this.description = description;
        this.name = name;
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(this.title)
                        .version(this.version)
                        .description(this.description)
                        .contact(new Contact()
                                .name(this.name)
                                .email("faccordoba@riu.com"))
                        .license( new License()
                                .name("Apache 2.0")
                                .url("https://springdoc.org")));
    }
}
