package org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.adapter.out.messaging;

import org.faccordoba.springcloud.msvc.msvcsearchriu.domain.model.SearchSession;
import org.faccordoba.springcloud.msvc.msvcsearchriu.domain.port.out.SearchEventPublisherPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


@Component
public class SearchKafkaPublisher implements SearchEventPublisherPort {
    private final KafkaTemplate<String, SearchSession> kafkaTemplate;
    @Value("${spring.kafka.topic.name}")
    private String topic;
    private final Logger logger = LoggerFactory.getLogger(SearchKafkaPublisher.class);

    public SearchKafkaPublisher(KafkaTemplate<String, SearchSession> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    @Override
    public void publishSearchEvent(SearchSession domainEvent) {
        logger.info("Publishing event to Kafka topic: {}", topic);
        // Enviar sincrono
        kafkaTemplate.send(topic, domainEvent)
                .thenAccept(result -> logger.info("Event published successfully: {}", result))
                .exceptionally(ex -> {
                    logger.error("Error publishing event: {}", ex.getMessage(), ex);
                    return null;
                });
    }

}