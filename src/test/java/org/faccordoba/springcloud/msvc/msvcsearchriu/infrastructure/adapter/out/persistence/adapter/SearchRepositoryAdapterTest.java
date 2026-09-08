package org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.adapter.out.persistence.adapter;

import org.faccordoba.springcloud.msvc.msvcsearchriu.domain.exception.SearchDomainException;
import org.faccordoba.springcloud.msvc.msvcsearchriu.domain.exception.SearchNotFoundException;
import org.faccordoba.springcloud.msvc.msvcsearchriu.domain.model.SearchSession;
import org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.adapter.out.persistence.entity.SearchSessionEntity;
import org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.adapter.out.persistence.mapper.SearchSessionMapper;
import org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.adapter.out.persistence.repository.SearchSessionJpaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchRepositoryAdapterTest {

    @Mock
    private SearchSessionMapper searchMapper;

    @Mock
    private SearchSessionJpaRepository searchSessionJpaRepository;

    @InjectMocks
    private SearchRepositoryAdapter searchRepositoryAdapter;

    private SearchSession domainSession;
    private SearchSessionEntity entitySession;

    @BeforeEach
    void setUp() {
        domainSession = new SearchSession(
                "repo-sess-1",
                "HOTEL01",
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 5),
                List.of(20, 22),
                0
        );
        entitySession = new SearchSessionEntity(
                "repo-sess-1",
                "HOTEL01",
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 5),
                "20,22",
                0
        );
    }

    @AfterEach
    void tearDown() {
        domainSession = null;
        entitySession = null;
    }

    @Test
    public void saveShouldPersistEntitySuccessfullyTest() {
        when(searchMapper.toEntity(domainSession)).thenReturn(entitySession);
        when(searchSessionJpaRepository.save(entitySession)).thenReturn(entitySession);

        searchRepositoryAdapter.save(domainSession);

        verify(searchMapper).toEntity(domainSession);
        verify(searchSessionJpaRepository).save(entitySession);
    }

    @Test
    public void saveShouldThrowSearchDomainExceptionWhenDataIntegrityViolationOccursTest() {
        when(searchMapper.toEntity(domainSession)).thenReturn(entitySession);
        when(searchSessionJpaRepository.save(entitySession))
                .thenThrow(new DataIntegrityViolationException("Constraint violation"));

        assertThatThrownBy(() -> searchRepositoryAdapter.save(domainSession))
                .isInstanceOf(SearchDomainException.class)
                .hasMessageContaining("Error saved session");

        verify(searchSessionJpaRepository).save(entitySession);
    }

    @Test
    public void findBySearchIdShouldReturnDomainModelWhenEntityExistsTest() {
        String searchId = "repo-sess-1";
        when(searchSessionJpaRepository.findBySearchId(searchId)).thenReturn(Optional.of(entitySession));
        when(searchMapper.toDomain(entitySession)).thenReturn(domainSession);

        Optional<SearchSession> result = searchRepositoryAdapter.findBySearchId(searchId);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(domainSession);
        verify(searchSessionJpaRepository).findBySearchId(searchId);
        verify(searchMapper).toDomain(entitySession);
    }

    @Test
    public void findBySearchIdShouldThrowSearchNotFoundExceptionWhenEntityNotFoundTest() {
        String searchId = "not-found-id";
        when(searchSessionJpaRepository.findBySearchId(searchId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> searchRepositoryAdapter.findBySearchId(searchId))
                .isInstanceOf(SearchNotFoundException.class)
                .hasMessageContaining(String.format("Search with id %s not found", searchId));

        verify(searchSessionJpaRepository).findBySearchId(searchId);
        verifyNoInteractions(searchMapper);
    }

    @Test
    public void updateVisitCountShouldUpdateVisitCountSuccessfullyTest() {
        String searchId = "repo-sess-1";
        int count = 10;

        searchRepositoryAdapter.updateVisitCount(searchId, count);

        verify(searchSessionJpaRepository).updateVisitCount(searchId, count);
    }

    @Test
    public void updateVisitCountShouldThrowSearchDomainExceptionWhenDatabaseErrorOccursTest() {
        String searchId = "repo-sess-1";
        int count = 10;
        doThrow(new RuntimeException("DB down"))
                .when(searchSessionJpaRepository).updateVisitCount(searchId, count);

        assertThatThrownBy(() -> searchRepositoryAdapter.updateVisitCount(searchId, count))
                .isInstanceOf(SearchDomainException.class)
                .hasMessageContaining("Error updated count");

        verify(searchSessionJpaRepository).updateVisitCount(searchId, count);
    }
}
