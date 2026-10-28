package org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.adapter.out.persistence.repository;

import org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.adapter.out.persistence.entity.SearchSessionEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class SearchSessionJpaRepositoryTest {

    @Autowired
    private SearchSessionJpaRepository repository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void updateVisitCountShouldPersistCountForStringSearchId() {
        String searchId = "repo-sess-1";
        SearchSessionEntity session = new SearchSessionEntity(
                searchId,
                "HOTEL01",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(2),
                "20,22",
                1
        );
        repository.saveAndFlush(session);

        repository.updateVisitCount(searchId, 10);
        entityManager.clear();

        assertThat(repository.findBySearchId(searchId))
                .get()
                .extracting(SearchSessionEntity::getVisitCount)
                .isEqualTo(10);
    }
}
