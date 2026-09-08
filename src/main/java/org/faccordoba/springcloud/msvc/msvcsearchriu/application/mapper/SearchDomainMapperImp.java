package org.faccordoba.springcloud.msvc.msvcsearchriu.application.mapper;

import org.faccordoba.springcloud.msvc.msvcsearchriu.domain.model.SearchSession;
import org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.adapter.in.web.dto.request.SearchRequest;
import org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.adapter.in.web.dto.response.CountSearchSessionResponse;
import org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.adapter.in.web.dto.response.Payload;
import org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.adapter.in.web.dto.response.SearchSessionResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class SearchDomainMapperImp implements SearchDomainMapper {

    @Override
    public SearchSession toDomain(SearchRequest searchRequest, String searchId) {
        return new SearchSession(
                searchId,
                searchRequest.hotelId(),
                LocalDate.parse(searchRequest.checkIn(),getDateTimeFormatter()),
                LocalDate.parse(searchRequest.checkOut(),getDateTimeFormatter()),
                searchRequest.ages(),
                null
        );
    }

    @Override
    public SearchSessionResponse toSearchSessionResponse(String searchId) {
        return new SearchSessionResponse(searchId);
    }

    @Override
    public CountSearchSessionResponse toCountSearchSessionResponse(SearchSession searchSession) {
        Payload payload =new Payload(searchSession.hotelId(),
                searchSession.checkIn().format(getDateTimeFormatter()),
                searchSession.checkOut().format(getDateTimeFormatter()),
                searchSession.ages());
        return new CountSearchSessionResponse(searchSession.sessionId(), payload, searchSession.count());
    }


    private DateTimeFormatter getDateTimeFormatter(){
        return DateTimeFormatter.ofPattern("dd/MM/yyyy");
    }
}
