package org.faccordoba.springcloud.msvc.msvcsearchriu.application.service;

import org.faccordoba.springcloud.msvc.msvcsearchriu.domain.model.SearchSession;
import org.faccordoba.springcloud.msvc.msvcsearchriu.domain.port.out.SearchEventPublisherPort;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SearchSessionServiceTest {

    @Mock
    private SearchEventPublisherPort searchEventPublisher;

    @InjectMocks
    private SearchSessionService searchSessionService;

    private SearchSession searchSession;

    @BeforeEach
    void setUp() {
        searchSession = new SearchSession(
                "session-123",
                "HOTEL01",
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 5),
                List.of(30, 28),
                0
        );
    }

    @AfterEach
    void tearDown() {
        searchSession = null;
    }

    @Test
    public void executeSearchShouldPublishEventAndReturnSessionIdTest() {
        String sessionId = searchSessionService.executeSearch(searchSession);

        assertThat(sessionId).isEqualTo("session-123");
        verify(searchEventPublisher).publishSearchEvent(searchSession);
    }
}
