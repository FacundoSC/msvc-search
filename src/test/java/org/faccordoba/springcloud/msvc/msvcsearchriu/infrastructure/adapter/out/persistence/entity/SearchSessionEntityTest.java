package org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.adapter.out.persistence.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class SearchSessionEntityTest {

    @Test
    public void searchSessionEntityShouldInstantiateWithParameterizedConstructorAndExposeFieldsTest() {
        LocalDate checkIn = LocalDate.of(2026, 7, 1);
        LocalDate checkOut = LocalDate.of(2026, 7, 5);

        SearchSessionEntity entity = new SearchSessionEntity(
                "entity-id-1",
                "HOTEL01",
                checkIn,
                checkOut,
                "25,30",
                3
        );

        assertThat(entity.getSearchId()).isEqualTo("entity-id-1");
        assertThat(entity.getHotelId()).isEqualTo("HOTEL01");
        assertThat(entity.getCheckIn()).isEqualTo(checkIn);
        assertThat(entity.getCheckOut()).isEqualTo(checkOut);
        assertThat(entity.getAgesSequence()).isEqualTo("25,30");
        assertThat(entity.getVisitCount()).isEqualTo(3);
    }

    @Test
    public void searchSessionEntityShouldSupportDefaultConstructorTest() {
        SearchSessionEntity entity = new SearchSessionEntity();
        assertThat(entity).isNotNull();
        assertThat(entity.getSearchId()).isNull();
    }

    @Test
    public void onCreateAndOnUpdateShouldSetTimestampsTest() {
        SearchSessionEntity entity = new SearchSessionEntity(
                "entity-id-2",
                "HOTEL02",
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 8, 5),
                "40",
                1
        );

        entity.onCreate();
        LocalDateTime createdAt = entity.getCreatedAt();
        LocalDateTime updatedAt = entity.getUpdatedAt();

        assertThat(createdAt).isNotNull();
        assertThat(updatedAt).isNotNull();

        entity.onUpdate();
        assertThat(entity.getUpdatedAt()).isNotNull();
    }
}
