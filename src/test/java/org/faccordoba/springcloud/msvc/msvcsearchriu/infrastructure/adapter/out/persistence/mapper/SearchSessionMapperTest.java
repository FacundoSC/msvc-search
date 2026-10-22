package org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.adapter.out.persistence.mapper;

import org.faccordoba.springcloud.msvc.msvcsearchriu.domain.model.SearchSession;
import org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.adapter.out.persistence.entity.SearchSessionEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

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
        void toEntityShouldConvertDomainModelToEntityTest() {
        SearchSession domain = new SearchSession(
                "map-sess-1",
                "HOTEL01",
                LocalDate.now(ZoneId.systemDefault()).plusDays(1),
                LocalDate.now(ZoneId.systemDefault()).plusDays(6),
                List.of(25, 30, 4),
                2
        );
        SearchSessionEntity entity = mapper.toEntity(domain);
        assertAll(
                () -> assertThat(entity).isNotNull(),
                () -> assertThat(entity.getSearchId()).isEqualTo("map-sess-1"),
                () -> assertThat(entity.getHotelId()).isEqualTo("HOTEL01"),
                () -> assertThat(entity.getCheckIn()).isEqualTo(LocalDate.now(ZoneId.systemDefault()).plusDays(1)),
                () -> assertThat(entity.getCheckOut()).isEqualTo(LocalDate.now(ZoneId.systemDefault()).plusDays(6)),
                () -> assertThat(entity.getAgesSequence()).isEqualTo("25,30,4"),
                () -> assertThat(entity.getVisitCount()).isEqualTo(2)
        );
    }

    @Test
        void toDomainShouldConvertEntityToDomainModelTest() {
        SearchSessionEntity entity = new SearchSessionEntity(
                "map-sess-2",
                "HOTEL02",
                LocalDate.now(ZoneId.systemDefault()).plusDays(1),
                LocalDate.now(ZoneId.systemDefault()).plusDays(6),
                "35,40",
                5
        );

        SearchSession domain = mapper.toDomain(entity);

        assertAll(
                () -> assertThat(domain).isNotNull(),
                () -> assertThat(domain.sessionId()).isEqualTo("map-sess-2"),
                () -> assertThat(domain.hotelId()).isEqualTo("HOTEL02"),
                () -> assertThat(domain.checkIn()).isEqualTo(LocalDate.now(ZoneId.systemDefault()).plusDays(1)),
                () -> assertThat(domain.checkOut()).isEqualTo(LocalDate.now(ZoneId.systemDefault()).plusDays(6)),
                () -> assertThat(domain.ages()).containsExactly(35, 40),
                () -> assertThat(domain.count()).isEqualTo(5)
        );
    }
}
