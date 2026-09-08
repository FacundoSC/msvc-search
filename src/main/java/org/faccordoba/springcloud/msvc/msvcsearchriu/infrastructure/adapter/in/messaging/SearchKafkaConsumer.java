package org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.adapter.in.messaging;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.faccordoba.springcloud.msvc.msvcsearchriu.domain.model.SearchSession;
import org.faccordoba.springcloud.msvc.msvcsearchriu.domain.port.out.SearchCachePort;
import org.faccordoba.springcloud.msvc.msvcsearchriu.domain.port.out.SearchRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class SearchKafkaConsumer {
    private final SearchRepositoryPort searchRepository;// Output Port to BD
    private final SearchCachePort searchCache; // Output Port to Cache
    private final ObjectMapper objectMapper;
    private final Logger logger = LoggerFactory.getLogger(SearchKafkaConsumer.class);

    public SearchKafkaConsumer(SearchRepositoryPort searchRepository, SearchCachePort searchCache, ObjectMapper objectMapper) {
        this.searchRepository = searchRepository;
        this.searchCache = searchCache;
        this.objectMapper = objectMapper;
    }
    @KafkaListener(topics = "${spring.kafka.topic.name}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeSearchEvent(String domainEvent) {
        try {
            SearchSession searchSession = objectMapper.readValue(domainEvent, SearchSession.class);

            Thread.startVirtualThread(() -> {
                logger.info("Processing search event in virtual thread: {}", searchSession);
                try {
                    // Try atomic increment; if no row updated, save then increment
                    if (searchSession.count().equals(0)) {
                        // No existing row — create
                        searchRepository.save(searchSession);
                        searchCache.putSearchSession(searchSession);
                    }
                    else  {
                        // Existing row - update  count
                        searchRepository.updateVisitCount(searchSession.sessionId(), searchSession.count());
                    }
                } catch (Exception ex) {
                    logger.error("Error applying increment for searchSession {}: {}", searchSession, ex.getMessage(), ex);
                }
            });
        } catch (Exception e) {
            logger.error("Error processing search event: {}", e.getMessage(), e);
        }
        logger.info("Search event received: {}", domainEvent);
    }

}