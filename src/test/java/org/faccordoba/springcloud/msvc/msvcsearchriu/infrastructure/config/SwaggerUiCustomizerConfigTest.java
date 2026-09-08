package org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.config;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springdoc.core.properties.SwaggerUiConfigParameters;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springdoc.core.properties.SwaggerUiOAuthProperties;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springdoc.webmvc.ui.SwaggerIndexPageTransformer;
import org.springdoc.webmvc.ui.SwaggerWelcomeCommon;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.resource.ResourceTransformerChain;
import org.springframework.web.servlet.resource.TransformedResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SwaggerUiCustomizerConfigTest {

    @TempDir
    Path tempDir;

    private SwaggerUiConfigParameters swaggerUiConfigParameters;
    private SwaggerIndexPageTransformer transformer;
    private HttpServletRequest request;
    private ResourceTransformerChain transformerChain;

    @BeforeEach
    void setUp() {
        SwaggerUiCustomizerConfig config = new SwaggerUiCustomizerConfig();
        swaggerUiConfigParameters = mock(SwaggerUiConfigParameters.class);
        when(swaggerUiConfigParameters.getConfigUrl()).thenReturn("/v3/api-docs");
        transformer = config.swaggerIndexPageTransformer(
                mock(SwaggerUiConfigProperties.class),
                mock(SwaggerUiOAuthProperties.class),
                swaggerUiConfigParameters,
                mock(SwaggerWelcomeCommon.class),
                mock(ObjectMapperProvider.class));
        request = mock(HttpServletRequest.class);
        transformerChain = mock(ResourceTransformerChain.class);
    }

    @Test
    public void swaggerUiCustomizerConfigShouldProvideSwaggerIndexPageTransformerTest() {
        assertThat(transformer).isNotNull();
        assertThat(transformer).isInstanceOf(SwaggerIndexPageTransformer.class);
    }

    @Test
    public void transformShouldInjectCustomCssIntoIndexHtmlTest() throws IOException {
        Resource indexHtml = writeFile("index.html",
                "<html><head><title>Swagger UI</title></head><body></body></html>");

        Resource result = transformer.transform(request, indexHtml, transformerChain);

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(TransformedResource.class);
        String content = new String(result.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        assertThat(content).contains("</head>");
        assertThat(content).contains("<style>");
        assertThat(content).contains(".swagger-ui .topbar .topbar-wrapper .link { display: none !important; }");
        assertThat(content).contains(".swagger-ui .footer { display: none !important; }");
    }

    @Test
    public void transformShouldPassThroughResourceThatIsNotIndexHtmlTest() throws IOException {
        Resource resource = writeFile("swagger-ui.css", ".topbar { display: block; }");

        Resource result = transformer.transform(request, resource, transformerChain);

        assertThat(result).isSameAs(resource);
    }

    @Test
    public void transformShouldPassThroughResourceWithoutFilenameTest() throws IOException {
        Resource resource = mock(Resource.class);
        when(resource.getURL()).thenReturn(tempDir.resolve("bogus.css").toUri().toURL());

        Resource result = transformer.transform(request, resource, transformerChain);

        assertThat(result).isSameAs(resource);
    }

    private Resource writeFile(String name, String content) throws IOException {
        Path file = tempDir.resolve(name);
        Files.writeString(file, content);
        return new FileSystemResource(file);
    }
}