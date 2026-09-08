package org.faccordoba.springcloud.msvc.msvcsearchriu.application.mapper;

import org.faccordoba.springcloud.msvc.msvcsearchriu.domain.model.SearchSession;
import org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.adapter.in.web.dto.request.SearchRequest;
import org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.adapter.in.web.dto.response.CountSearchSessionResponse;
import org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.adapter.in.web.dto.response.SearchSessionResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SearchDomainMapperTest {

    private SearchDomainMapperImp mapper;

    @BeforeEach
    void setUp() {
        mapper = new SearchDomainMapperImp();
    }

    @AfterEach
    void tearDown() {
        mapper = null;
    }

    @Test
    public void toDomainShouldMapSearchRequestToSearchSessionTest() {
        SearchRequest request = new SearchRequest(
                "HOTEL01",
                "01/10/2026",
                "05/10/2026",
                List.of(30, 25)
        );
        String searchId = "test-id-123";

        SearchSession session = mapper.toDomain(request, searchId);

        assertThat(session).isNotNull();
        assertThat(session.sessionId()).isEqualTo("test-id-123");
        assertThat(session.hotelId()).isEqualTo("HOTEL01");
        assertThat(session.checkIn()).isEqualTo(LocalDate.of(2026, 10, 1));
        assertThat(session.checkOut()).isEqualTo(LocalDate.of(2026, 10, 5));
        assertThat(session.ages()).containsExactly(30, 25);
        assertThat(session.count()).isEqualTo(0);
    }

    @Test
    public void toSearchSessionResponseShouldMapSearchIdToResponseTest() {
        String searchId = "abc-xyz";

        SearchSessionResponse response = mapper.toSearchSessionResponse(searchId);

        assertThat(response).isNotNull();
        assertThat(response.searchId()).isEqualTo("abc-xyz");
    }

    @Test
    public void toCountSearchSessionResponseShouldMapSearchSessionToCountResponseTest() {
        SearchSession session = new SearchSession(
                "session-count-1",
                "HOTEL02",
                LocalDate.of(2026, 11, 1),
                LocalDate.of(2026, 11, 10),
                List.of(35, 32, 6),
                7
        );

        CountSearchSessionResponse response = mapper.toCountSearchSessionResponse(session);

        assertThat(response).isNotNull();
        assertThat(response.searchId()).isEqualTo("session-count-1");
        assertThat(response.count()).isEqualTo(7);
        assertThat(response.search()).isNotNull();
        assertThat(response.search().hotelId()).isEqualTo("HOTEL02");
        assertThat(response.search().checkIn()).isEqualTo("01/11/2026");
        assertThat(response.search().checkOut()).isEqualTo("10/11/2026");
        assertThat(response.search().ages()).containsExactly(35, 32, 6);
    }
}
