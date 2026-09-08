package org.faccordoba.springcloud.msvc.msvcsearchriu.application.service;

import org.faccordoba.springcloud.msvc.msvcsearchriu.application.exception.SearchNotFoundException;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchQueryServiceTest {

    @Mock
    private SearchRepositoryPort searchRepository;

    @Mock
    private SearchCachePort searchCache;

    @InjectMocks
    private SearchQueryService searchQueryService;

    private String searchId;
    private SearchSession baseSession;

    @BeforeEach
    void setUp() {
        searchId = "search-456";
        baseSession = new SearchSession(
                searchId,
                "HOTEL02",
                LocalDate.of(2026, 11, 10),
                LocalDate.of(2026, 11, 15),
                List.of(25, 27),
                2
        );
    }

    @AfterEach
    void tearDown() {
        searchId = null;
        baseSession = null;
    }

    @Test
    public void countShouldReturnUpdatedSessionWhenSessionExistsInCacheTest() {
        when(searchCache.getSearchSession(searchId)).thenReturn(Optional.of(baseSession));
        when(searchCache.incrementCount(searchId)).thenReturn(3);

        SearchSession result = searchQueryService.count(searchId);

        assertThat(result).isNotNull();
        assertThat(result.sessionId()).isEqualTo(searchId);
        assertThat(result.hotelId()).isEqualTo("HOTEL02");
        assertThat(result.count()).isEqualTo(3);

        verify(searchCache).incrementCount(searchId);
        verify(searchCache).markAsDirty(searchId);
        verifyNoInteractions(searchRepository);
    }

    @Test
    public void countShouldFetchFromDbAndPopulateCacheWhenCacheMissTest() {
        when(searchCache.getSearchSession(searchId)).thenReturn(Optional.empty());
        when(searchRepository.findBySearchId(searchId)).thenReturn(Optional.of(baseSession));
        when(searchCache.incrementCount(searchId)).thenReturn(3);

        SearchSession result = searchQueryService.count(searchId);

        assertThat(result).isNotNull();
        assertThat(result.sessionId()).isEqualTo(searchId);
        assertThat(result.count()).isEqualTo(3);

        verify(searchRepository).findBySearchId(searchId);
        verify(searchCache).putSearchSession(baseSession);
        verify(searchCache).incrementCount(searchId);
        verify(searchCache).markAsDirty(searchId);
    }

    @Test
    public void countShouldThrowSearchNotFoundExceptionWhenNotFoundInCacheAndDbTest() {
        when(searchCache.getSearchSession(searchId)).thenReturn(Optional.empty());
        when(searchRepository.findBySearchId(searchId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> searchQueryService.count(searchId))
                .isInstanceOf(SearchNotFoundException.class)
                .hasMessageContaining("Search session not found for id: " + searchId);

        verify(searchCache).getSearchSession(searchId);
        verify(searchRepository).findBySearchId(searchId);
        verify(searchCache, never()).putSearchSession(any());
        verify(searchCache, never()).incrementCount(any());
        verify(searchCache, never()).markAsDirty(any());
    }
}
