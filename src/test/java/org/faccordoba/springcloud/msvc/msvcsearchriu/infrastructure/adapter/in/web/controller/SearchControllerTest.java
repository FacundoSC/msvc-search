package org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.adapter.in.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.faccordoba.springcloud.msvc.msvcsearchriu.application.exception.SearchNotFoundException;
import org.faccordoba.springcloud.msvc.msvcsearchriu.application.mapper.SearchDomainMapper;
import org.faccordoba.springcloud.msvc.msvcsearchriu.domain.model.SearchSession;
import org.faccordoba.springcloud.msvc.msvcsearchriu.domain.port.in.SearchCommandUseCase;
import org.faccordoba.springcloud.msvc.msvcsearchriu.domain.port.in.SearchQueryUseCase;
import org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.adapter.in.web.dto.request.SearchRequest;
import org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.adapter.in.web.dto.response.CountSearchSessionResponse;
import org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.adapter.in.web.dto.response.Payload;
import org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.adapter.in.web.dto.response.SearchSessionResponse;
import org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class SearchControllerTest {

    @Mock
    private SearchCommandUseCase searchCommandUseCase;

    @Mock
    private SearchQueryUseCase searchQueryUseCase;

    @Mock
    private SearchDomainMapper searchDomainMapper;

    @InjectMocks
    private SearchController searchController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(searchController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @AfterEach
    void tearDown() {
        mockMvc = null;
        objectMapper = null;
    }

    @Test
    public void searchShouldReturn200AndSearchIdWhenRequestIsValidTest() throws Exception {
        SearchRequest request = new SearchRequest("HOTEL123", "01/10/2026", "05/10/2026", List.of(30, 25));
        SearchSession session = new SearchSession("gen-id-1", "HOTEL123", LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 5), List.of(30, 25), 0);

        when(searchDomainMapper.toDomain(any(SearchRequest.class), anyString())).thenReturn(session);
        when(searchCommandUseCase.executeSearch(session)).thenReturn("gen-id-1");
        when(searchDomainMapper.toSearchSessionResponse("gen-id-1")).thenReturn(new SearchSessionResponse("gen-id-1"));

        mockMvc.perform(post("/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.searchId").value("gen-id-1"));
    }

    @Test
    public void searchShouldReturn400WhenValidationFailsTest() throws Exception {
        SearchRequest invalidRequest = new SearchRequest("", "invalid-date", "05/10/2026", List.of());

        mockMvc.perform(post("/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Failed"));
    }

    @Test
    public void findSearchShouldReturn200AndCountResponseWhenSearchExistsTest() throws Exception {
        String searchId = "abc1234";
        SearchSession session = new SearchSession(searchId, "HOTEL123", LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 5), List.of(30, 25), 3);
        Payload payload = new Payload("HOTEL123", "01/10/2026", "05/10/2026", List.of(30, 25));
        CountSearchSessionResponse response = new CountSearchSessionResponse(searchId, payload, 3);

        when(searchQueryUseCase.count(searchId)).thenReturn(session);
        when(searchDomainMapper.toCountSearchSessionResponse(session)).thenReturn(response);

        mockMvc.perform(get("/count")
                        .param("searchId", searchId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.searchId").value(searchId))
                .andExpect(jsonPath("$.count").value(3))
                .andExpect(jsonPath("$.search.hotelId").value("HOTEL123"));
    }

    @Test
    public void findSearchShouldReturn404WhenSearchDoesNotExistTest() throws Exception {
        String searchId = "unknown-id";
        when(searchQueryUseCase.count(searchId)).thenThrow(new SearchNotFoundException("Search session not found for id: " + searchId));

        mockMvc.perform(get("/count")
                        .param("searchId", searchId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
