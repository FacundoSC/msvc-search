package org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.adapter.in.web.dto;

import org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.adapter.in.web.dto.request.SearchRequest;
import org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.adapter.in.web.dto.response.CountSearchSessionResponse;
import org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.adapter.in.web.dto.response.Payload;
import org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.adapter.in.web.dto.response.SearchSessionResponse;
import org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.exception.ErrorResponse;
import org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.exception.ValidationErrorResponse;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class DtoAndResponseModelsTest {

    @Test
    public void searchRequestShouldInstantiateAndExposeFieldsTest() {
        SearchRequest request = new SearchRequest("HOTEL1", "01/01/2026", "05/01/2026", List.of(20, 30));

        assertThat(request.hotelId()).isEqualTo("HOTEL1");
        assertThat(request.checkIn()).isEqualTo("01/01/2026");
        assertThat(request.checkOut()).isEqualTo("05/01/2026");
        assertThat(request.ages()).containsExactly(20, 30);
    }

    @Test
    public void searchSessionResponseShouldInstantiateAndExposeFieldsTest() {
        SearchSessionResponse response = new SearchSessionResponse("sess-001");

        assertThat(response.searchId()).isEqualTo("sess-001");
    }

    @Test
    public void payloadShouldInstantiateAndExposeFieldsTest() {
        Payload payload = new Payload("HOTEL2", "10/02/2026", "15/02/2026", List.of(40));

        assertThat(payload.hotelId()).isEqualTo("HOTEL2");
        assertThat(payload.checkIn()).isEqualTo("10/02/2026");
        assertThat(payload.checkOut()).isEqualTo("15/02/2026");
        assertThat(payload.ages()).containsExactly(40);
    }

    @Test
    public void countSearchSessionResponseShouldInstantiateAndExposeFieldsTest() {
        Payload payload = new Payload("HOTEL3", "20/03/2026", "25/03/2026", List.of(22, 23));
        CountSearchSessionResponse response = new CountSearchSessionResponse("sess-002", payload, 10);

        assertThat(response.searchId()).isEqualTo("sess-002");
        assertThat(response.search()).isEqualTo(payload);
        assertThat(response.count()).isEqualTo(10);
    }

    @Test
    public void errorResponseShouldInstantiateAndExposeFieldsTest() {
        LocalDateTime now = LocalDateTime.now();
        ErrorResponse errorResponse = new ErrorResponse(now, 404, "Not Found", "Session not found", "/search");

        assertThat(errorResponse.timestamp()).isEqualTo(now);
        assertThat(errorResponse.status()).isEqualTo(404);
        assertThat(errorResponse.error()).isEqualTo("Not Found");
        assertThat(errorResponse.message()).isEqualTo("Session not found");
        assertThat(errorResponse.path()).isEqualTo("/search");
    }

    @Test
    public void validationErrorResponseShouldInstantiateAndExposeFieldsTest() {
        LocalDateTime now = LocalDateTime.now();
        Map<String, String> errors = Map.of("hotelId", "Hotel ID is mandatory");
        ValidationErrorResponse valResponse = new ValidationErrorResponse(now, 400, "Validation Failed", "Invalid input", "/search", errors);

        assertThat(valResponse.timestamp()).isEqualTo(now);
        assertThat(valResponse.status()).isEqualTo(400);
        assertThat(valResponse.error()).isEqualTo("Validation Failed");
        assertThat(valResponse.message()).isEqualTo("Invalid input");
        assertThat(valResponse.path()).isEqualTo("/search");
        assertThat(valResponse.errors()).isEqualTo(errors);
    }
}
