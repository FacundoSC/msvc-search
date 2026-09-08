package org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.adapter.out.messaging;

import org.faccordoba.springcloud.msvc.msvcsearchriu.domain.model.SearchSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchKafkaPublisherTest {

    @Mock
    private KafkaTemplate<String, SearchSession> kafkaTemplate;

    private SearchKafkaPublisher publisher;
    private SearchSession session;

    @BeforeEach
    void setUp() {
        publisher = new SearchKafkaPublisher(kafkaTemplate);
        ReflectionTestUtils.setField(publisher, "topic", "hotel-search-events");

        session = new SearchSession(
                "pub-sess-1",
                "HOTEL01",
                LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 9, 5),
                List.of(25, 30),
                0
        );
    }

    @AfterEach
    void tearDown() {
        publisher = null;
        session = null;
    }

    @Test
    public void publishSearchEventShouldSendEventToConfiguredTopicTest() {
        @SuppressWarnings("unchecked")
        SendResult<String, SearchSession> sendResult = mock(SendResult.class);
        when(kafkaTemplate.send("hotel-search-events", session))
                .thenReturn(CompletableFuture.completedFuture(sendResult));

        publisher.publishSearchEvent(session);

        verify(kafkaTemplate).send("hotel-search-events", session);
    }

    @Test
    public void publishSearchEventShouldHandleFutureFailureGracefullyTest() {
        CompletableFuture<SendResult<String, SearchSession>> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("Kafka broker unavailable"));

        when(kafkaTemplate.send("hotel-search-events", session))
                .thenReturn(failedFuture);

        publisher.publishSearchEvent(session);

        verify(kafkaTemplate).send("hotel-search-events", session);
    }
}
