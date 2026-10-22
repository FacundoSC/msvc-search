package org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class OpenApiConfigTest {
    @Value("${api.info.title}")
    private String title;
    @Value("${api.info.version}")
    private String version;
    @Value("${api.info.description}")
    private String description;
    @Value("${api.info.name}")
    private String name;

    @Test
     void openApiConfigShouldProvideCustomOpenAPITest() {
        OpenApiConfig config = new OpenApiConfig(title, version, description, name);
        OpenAPI openAPI = config.customOpenAPI();

        assertAll(
                () -> assertThat(openAPI).isNotNull(),
                () -> assertThat(openAPI.getInfo()).isNotNull(),
                () -> assertThat(openAPI.getInfo().getTitle()).isEqualTo(title),
                () -> assertThat(openAPI.getInfo().getDescription()).isEqualTo(description),
                () -> assertThat(openAPI.getInfo().getVersion()).isEqualTo(version),
                () -> assertThat(openAPI.getInfo().getContact().getName()).isEqualTo(name)
        );
    }

}