package org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.adapter.out.persistence.mapper;

import org.faccordoba.springcloud.msvc.msvcsearchriu.domain.model.SearchSession;
import org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.adapter.out.persistence.entity.SearchSessionEntity;
import org.springframework.stereotype.Component;


@Component
public class SearchSessionMapperImpl implements  SearchSessionMapper {

    @Override
    public SearchSessionEntity toEntity(SearchSession searchSession) {
        String agesSequence = searchSession.ages()
                .stream().map(String::valueOf)
                .reduce((a, b) -> a + "," + b)
                .orElse("");
        return  new SearchSessionEntity(
                searchSession.sessionId(),
                searchSession.hotelId(),
                searchSession.checkIn(),
                searchSession.checkOut(),
                agesSequence,
                searchSession.count());

    }

    @Override
    public SearchSession toDomain(SearchSessionEntity searchSessionEntity) {
        String[] agesArray = searchSessionEntity.getAgesSequence().split(",");
        return new SearchSession(
                searchSessionEntity.getSearchId(),
                searchSessionEntity.getHotelId(),
                searchSessionEntity.getCheckIn(),
                searchSessionEntity.getCheckOut(),
                java.util.Arrays.stream(agesArray)
                        .filter(s -> !s.isEmpty())
                        .map(Integer::parseInt)
                        .toList(),
                searchSessionEntity.getVisitCount()
        );
    }
}
