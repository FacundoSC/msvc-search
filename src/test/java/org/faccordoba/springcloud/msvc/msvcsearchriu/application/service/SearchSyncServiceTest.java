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
class SearchSyncServiceTest {

    @Mock
    private SearchCachePort searchCache;

    @Mock
    private SearchRepositoryPort searchRepository;

    @InjectMocks
    private SearchSyncService searchSyncService;

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
    void syncCountsToDatabaseShouldDoNothingWhenNoDirtyIdsTest() {
        when(searchCache.getDirtyIds()).thenReturn(List.of());

        searchSyncService.syncDirtyCounts();

        verify(searchCache).getDirtyIds();
        verifyNoMoreInteractions(searchCache);
        verifyNoInteractions(searchRepository);
    }

    @Test
    void syncCountsToDatabaseShouldUpdateDatabaseAndClearDirtyWhenDirtyIdsPresentTest() {
        when(searchCache.getDirtyIds()).thenReturn(List.of("sync-1"));
        when(searchCache.getSearchSession("sync-1")).thenReturn(Optional.of(session));

        searchSyncService.syncDirtyCounts();

        verify(searchRepository).updateVisitCount("sync-1", 5);
        verify(searchCache).clearDirty("sync-1");
    }

    @Test
    void syncCountsToDatabaseShouldSkipWhenSessionNotPresentInCacheTest() {
        when(searchCache.getDirtyIds()).thenReturn(List.of("sync-missing"));
        when(searchCache.getSearchSession("sync-missing")).thenReturn(Optional.empty());

        searchSyncService.syncDirtyCounts();

        verify(searchCache).getSearchSession("sync-missing");
        verify(searchRepository, never()).updateVisitCount(anyString(), anyInt());
        verify(searchCache, never()).clearDirty("sync-missing");
    }

    @Test
    void syncCountsToDatabaseShouldCatchExceptionAndNotClearDirtyWhenErrorOccursTest() {
        when(searchCache.getDirtyIds()).thenReturn(List.of("sync-error"));
        when(searchCache.getSearchSession("sync-error")).thenReturn(Optional.of(session));
        doThrow(new RuntimeException("Database error"))
                .when(searchRepository).updateVisitCount("sync-error", 5);

        searchSyncService.syncDirtyCounts();

        verify(searchRepository).updateVisitCount("sync-error", 5);
        verify(searchCache, never()).clearDirty("sync-error");
    }
}
