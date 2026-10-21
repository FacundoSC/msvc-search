package org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class ObjectMapperConfigTest {

    @Test
     void objectMapperConfigShouldProvidedModuleJacksonDataTypeJsr310Test() {
        String moduleId = "jackson-datatype-jsr310";
        ObjectMapperConfig config = new ObjectMapperConfig();
        ObjectMapper objectMapper = config.objectMapper();
        assertAll(
                () -> assertThat(objectMapper).isNotNull(),
                () -> assertThat(objectMapper.getRegisteredModuleIds()).contains(moduleId)
        );
    }


}