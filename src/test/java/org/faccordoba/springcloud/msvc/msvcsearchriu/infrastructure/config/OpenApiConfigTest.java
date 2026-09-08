package org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OpenApiConfigTest {

    @Test
    public void openApiConfigShouldProvideCustomOpenAPITest() {
        String title = "Search Microservice API - RIU Hotels & Resorts";
        String description = "API for Search Microservice API - RIU Hotels & Resorts";
        String version = "1.0.0";
        String name = "Facundo Cordoba";
        OpenApiConfig config = new OpenApiConfig();
        OpenAPI openAPI = config.customOpenAPI();

        assertThat(openAPI).isNotNull();
        assertThat(openAPI.getInfo()).isNotNull();
        assertThat(openAPI.getInfo().getTitle()).isEqualTo(title);
        assertThat(openAPI.getInfo().getDescription()).isEqualTo(description);
        assertThat(openAPI.getInfo().getVersion()).isEqualTo(version);
        assertThat(openAPI.getInfo().getContact().getName()).isEqualTo(name);
    }

}