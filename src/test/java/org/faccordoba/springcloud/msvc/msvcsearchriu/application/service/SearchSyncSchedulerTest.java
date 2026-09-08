package org.faccordoba.springcloud.msvc.msvcsearchriu.application.service;

import org.faccordoba.springcloud.msvc.msvcsearchriu.domain.model.SearchSession;
import org.faccordoba.springcloud.msvc.msvcsearchriu.domain.port.out.SearchCachePort;
import org.faccordoba.springcloud.msvc.msvcsearchriu.domain.port.out.SearchRepositoryPort;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchSyncSchedulerTest {

    @Mock
    private SearchCachePort searchCache;

    @Mock
    private SearchRepositoryPort searchRepository;

    @InjectMocks
    private SearchSyncScheduler searchSyncScheduler;

    private SearchSession session;

    @BeforeEach
    void setUp() {
        session = new SearchSession(
                "sync-1",
                "HOTEL01",
                LocalDate.of(2026, 12, 1),
                LocalDate.of(2026, 12, 5),
                List.of(20),
                5
        );
    }

    @AfterEach
    void tearDown() {
        session = null;
    }

    @Test
    public void syncCountsToDatabaseShouldDoNothingWhenNoDirtyIdsTest() {
        when(searchCache.getDirtyIds()).thenReturn(List.of());

        searchSyncScheduler.syncCountsToDatabase();

        verify(searchCache).getDirtyIds();
        verifyNoMoreInteractions(searchCache);
        verifyNoInteractions(searchRepository);
    }

    @Test
    public void syncCountsToDatabaseShouldUpdateDatabaseAndClearDirtyWhenDirtyIdsPresentTest() {
        when(searchCache.getDirtyIds()).thenReturn(List.of("sync-1"));
        when(searchCache.getSearchSession("sync-1")).thenReturn(Optional.of(session));

        searchSyncScheduler.syncCountsToDatabase();

        verify(searchRepository).updateVisitCount("sync-1", 5);
        verify(searchCache).clearDirty("sync-1");
    }

    @Test
    public void syncCountsToDatabaseShouldSkipWhenSessionNotPresentInCacheTest() {
        when(searchCache.getDirtyIds()).thenReturn(List.of("sync-missing"));
        when(searchCache.getSearchSession("sync-missing")).thenReturn(Optional.empty());

        searchSyncScheduler.syncCountsToDatabase();

        verify(searchCache).getSearchSession("sync-missing");
        verify(searchRepository, never()).updateVisitCount(anyString(), anyInt());
        verify(searchCache, never()).clearDirty("sync-missing");
    }

    @Test
    public void syncCountsToDatabaseShouldCatchExceptionAndNotClearDirtyWhenErrorOccursTest() {
        when(searchCache.getDirtyIds()).thenReturn(List.of("sync-error"));
        when(searchCache.getSearchSession("sync-error")).thenReturn(Optional.of(session));
        doThrow(new RuntimeException("Database error"))
                .when(searchRepository).updateVisitCount("sync-error", 5);

        searchSyncScheduler.syncCountsToDatabase();

        verify(searchRepository).updateVisitCount("sync-error", 5);
        verify(searchCache, never()).clearDirty("sync-error");
    }
}
