package org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springdoc.core.properties.SwaggerUiConfigParameters;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springdoc.core.properties.SwaggerUiOAuthProperties;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springdoc.webmvc.ui.SwaggerIndexPageTransformer;
import org.springdoc.webmvc.ui.SwaggerWelcomeCommon;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.resource.ResourceTransformerChain;
import org.springframework.web.servlet.resource.TransformedResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
public class SwaggerUiCustomizerConfig {

    private static final String CUSTOM_CSS = """
            <style>
              .swagger-ui .topbar .topbar-wrapper .link { display: none !important; }
              .swagger-ui .footer { display: none !important; }
            </style>
            """;

    @Bean
    public SwaggerIndexPageTransformer swaggerIndexPageTransformer(
            SwaggerUiConfigProperties swaggerUiConfigProperties,
            SwaggerUiOAuthProperties swaggerUiOAuthProperties,
            SwaggerUiConfigParameters swaggerUiConfigParameters,
            SwaggerWelcomeCommon swaggerWelcomeCommon,
            ObjectMapperProvider objectMapperProvider) {
        return new SwaggerIndexPageTransformer(
                swaggerUiConfigProperties,
                swaggerUiOAuthProperties,
                swaggerUiConfigParameters,
                swaggerWelcomeCommon,
                objectMapperProvider) {
            @Override
            public Resource transform(HttpServletRequest request, Resource resource,
                                      ResourceTransformerChain transformerChain) throws IOException {
                Resource transformed = super.transform(request, resource, transformerChain);
                if (transformed != null && resource.getFilename() != null
                        && resource.getFilename().equalsIgnoreCase("index.html")) {
                    String html = new String(transformed.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                    html = html.replace("</head>", CUSTOM_CSS + "</head>");
                    return new TransformedResource(resource, html.getBytes(StandardCharsets.UTF_8));
                }
                return transformed;
            }
        };
    }
}