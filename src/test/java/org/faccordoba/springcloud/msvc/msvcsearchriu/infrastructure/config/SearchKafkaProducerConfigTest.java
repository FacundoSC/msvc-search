package org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.config;

import org.faccordoba.springcloud.msvc.msvcsearchriu.domain.model.SearchSession;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class SearchKafkaProducerConfigTest {

    @Test
    public void searchKafkaProducerConfigShouldCreateProducerFactoryAndTemplateTest() {
        SearchKafkaProducerConfig config = new SearchKafkaProducerConfig();
        ReflectionTestUtils.setField(config, "bootstrapServers", "localhost:9092");
        ReflectionTestUtils.setField(config, "keySerializer", "org.apache.kafka.common.serialization.StringSerializer");
        ReflectionTestUtils.setField(config, "valueSerializer", "org.springframework.kafka.support.serializer.JsonSerializer");

        ProducerFactory<String, SearchSession> producerFactory = config.searchProducerFactory();
        KafkaTemplate<String, SearchSession> template = config.kafkaTemplate();

        assertThat(producerFactory).isNotNull();
        assertThat(template).isNotNull();
    }

}