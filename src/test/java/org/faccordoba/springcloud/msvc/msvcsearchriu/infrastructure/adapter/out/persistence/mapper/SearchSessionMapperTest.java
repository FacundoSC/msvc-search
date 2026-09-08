package org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.adapter.out.persistence.mapper;

import org.faccordoba.springcloud.msvc.msvcsearchriu.domain.model.SearchSession;
import org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.adapter.out.persistence.entity.SearchSessionEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SearchSessionMapperTest {

    private SearchSessionMapperImpl mapper;

    @BeforeEach
    void setUp() {
        mapper = new SearchSessionMapperImpl();
    }

    @AfterEach
    void tearDown() {
        mapper = null;
    }

    @Test
    public void toEntityShouldConvertDomainModelToEntityTest() {
        SearchSession domain = new SearchSession(
                "map-sess-1",
                "HOTEL01",
                LocalDate.of(2026, 9, 10),
                LocalDate.of(2026, 9, 15),
                List.of(25, 30, 4),
                2
        );

        SearchSessionEntity entity = mapper.toEntity(domain);

        assertThat(entity).isNotNull();
        assertThat(entity.getSearchId()).isEqualTo("map-sess-1");
        assertThat(entity.getHotelId()).isEqualTo("HOTEL01");
        assertThat(entity.getCheckIn()).isEqualTo(LocalDate.of(2026, 9, 10));
        assertThat(entity.getCheckOut()).isEqualTo(LocalDate.of(2026, 9, 15));
        assertThat(entity.getAgesSequence()).isEqualTo("25,30,4");
        assertThat(entity.getVisitCount()).isEqualTo(2);
    }

    @Test
    public void toDomainShouldConvertEntityToDomainModelTest() {
        SearchSessionEntity entity = new SearchSessionEntity(
                "map-sess-2",
                "HOTEL02",
                LocalDate.of(2026, 9, 20),
                LocalDate.of(2026, 9, 25),
                "35,40",
                5
        );

        SearchSession domain = mapper.toDomain(entity);

        assertThat(domain).isNotNull();
        assertThat(domain.sessionId()).isEqualTo("map-sess-2");
        assertThat(domain.hotelId()).isEqualTo("HOTEL02");
        assertThat(domain.checkIn()).isEqualTo(LocalDate.of(2026, 9, 20));
        assertThat(domain.checkOut()).isEqualTo(LocalDate.of(2026, 9, 25));
        assertThat(domain.ages()).containsExactly(35, 40);
        assertThat(domain.count()).isEqualTo(5);
    }
}
