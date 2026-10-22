package org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.adapter.in.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.faccordoba.springcloud.msvc.msvcsearchriu.domain.model.SearchSession;
import org.faccordoba.springcloud.msvc.msvcsearchriu.domain.port.out.SearchCachePort;
import org.faccordoba.springcloud.msvc.msvcsearchriu.domain.port.out.SearchRepositoryPort;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchKafkaConsumerTest {
    @Mock private SearchRepositoryPort searchRepository;
    @Mock private SearchCachePort searchCache;
    private ObjectMapper objectMapper;
    private SearchKafkaConsumer consumer;
    private LocalDate checkIn;
    private LocalDate checkOut;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        consumer = new SearchKafkaConsumer(searchRepository, searchCache, objectMapper);
        checkIn = LocalDate.now(ZoneId.systemDefault()).plusDays(1);
        checkOut = LocalDate.now(ZoneId.systemDefault()).plusDays(4);
    }

    @AfterEach
    void tearDown() {
        consumer = null;
        objectMapper = null;
    }

    @Test
     void consumeSearchEventShouldSaveAndCacheWhenCountIsZeroTest() throws Exception {
        SearchSession session = new SearchSession(
                "kafka-sess-0",
                "HOTEL01",
                checkIn,
                checkOut,
                List.of(30),
                0
        );
        String jsonEvent = objectMapper.writeValueAsString(session);

        consumer.consumeSearchEvent(jsonEvent);

        verify(searchRepository, timeout(2000)).save(session);
        verify(searchCache, timeout(2000)).putSearchSession(session);
    }

    @Test
     void consumeSearchEventShouldUpdateVisitCountWhenCountIsGreaterThanZeroTest() throws Exception {
        SearchSession session = new SearchSession(
                "kafka-sess-1",
                "HOTEL01",
                checkIn,
                checkOut,
                List.of(30),
                4
        );
        String jsonEvent = objectMapper.writeValueAsString(session);

        consumer.consumeSearchEvent(jsonEvent);

        verify(searchRepository, timeout(2000)).updateVisitCount("kafka-sess-1", 4);
        verify(searchCache, never()).putSearchSession(any());
    }

    @Test
     void consumeSearchEventShouldHandleJsonParseExceptionGracefullyTest() {
        String invalidJson = "{invalid-json-payload}";

        consumer.consumeSearchEvent(invalidJson);

        verifyNoInteractions(searchRepository);
        verifyNoInteractions(searchCache);
    }

    @Test
     void consumeSearchEventShouldHandleRepositoryExceptionInVirtualThreadGracefullyTest() throws Exception {
        SearchSession session = new SearchSession(
                "kafka-sess-err",
                "HOTEL01",
                checkIn,
                checkOut,
                List.of(30),
                0
        );
        String jsonEvent = objectMapper.writeValueAsString(session);
        doThrow(new RuntimeException("Database error")).when(searchRepository).save(any(SearchSession.class));

        consumer.consumeSearchEvent(jsonEvent);

        verify(searchRepository, timeout(2000)).save(session);
    }
}
