package org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.config;

import org.junit.jupiter.api.Test;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class SearchKafkaConsumerConfigTest {

    @Test
    public void searchKafkaConsumerConfigShouldCreateConsumerFactoryAndListenerContainerFactoryTest() {
        SearchKafkaConsumerConfig config = new SearchKafkaConsumerConfig();
        ReflectionTestUtils.setField(config, "bootstrapServers", "localhost:9092");
        ReflectionTestUtils.setField(config, "groupId", "search-consumer-group");

        ConsumerFactory<String, String> consumerFactory = config.consumerFactory();
        ConcurrentKafkaListenerContainerFactory<String, String> containerFactory = config.kafkaListenerContainerFactory();

        assertThat(consumerFactory).isNotNull();
        assertThat(containerFactory).isNotNull();
    }
}