package org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.adapter.in.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.faccordoba.springcloud.msvc.msvcsearchriu.application.mapper.SearchDomainMapper;
import org.faccordoba.springcloud.msvc.msvcsearchriu.domain.model.SearchSession;
import org.faccordoba.springcloud.msvc.msvcsearchriu.domain.port.in.SearchCommandUseCase;
import org.faccordoba.springcloud.msvc.msvcsearchriu.domain.port.in.SearchQueryUseCase;
import org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.adapter.in.web.dto.request.SearchRequest;
import org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.adapter.in.web.dto.response.CountSearchSessionResponse;
import org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.adapter.in.web.dto.response.SearchSessionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@Tag(name = "Search Sessions", description = "Operaciones para registrar y consultar sesiones de búsqueda de hoteles")
public class SearchController {

    private final SearchCommandUseCase searchCommandUseCase;
    private final SearchQueryUseCase searchQueryUseCase;
    private final SearchDomainMapper searchDomainMapper;

    public SearchController(SearchCommandUseCase searchCommandUseCase,
                            SearchQueryUseCase searchQueryUseCase,
                            SearchDomainMapper searchDomainMapper) {
        this.searchCommandUseCase = searchCommandUseCase;
        this.searchQueryUseCase = searchQueryUseCase;
        this.searchDomainMapper = searchDomainMapper;
    }

    @Operation(
            summary = "Registrar nueva búsqueda",
            description = "Crea una nueva sesión de búsqueda con un ID único y publica el evento de búsqueda en Kafka."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Búsqueda registrada exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SearchSessionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos (ej. fechas inválidas o parámetros incompletos)",
                    content = @Content(mediaType = "application/json")
            )
    })
    @PostMapping("/search")
    public ResponseEntity<SearchSessionResponse> search(@Valid @RequestBody SearchRequest searchRequest) {
        // 1. generate a unique search ID
        String searchId = UUID.randomUUID().toString().substring(0, 7);
        // 2. validate and Map the search request to the domain model
        SearchSession searchSession = searchDomainMapper.toDomain(searchRequest, searchId);
        return ResponseEntity.ok(searchDomainMapper.toSearchSessionResponse(searchCommandUseCase.executeSearch(searchSession)));
    }

    @Operation(
            summary = "Consultar conteo de visitas de una búsqueda",
            description = "Devuelve los datos de la búsqueda y la cantidad de veces que ha sido consultada."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Detalle y conteo de la búsqueda obtenido con éxito",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CountSearchSessionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Sesión de búsqueda no encontrada para el searchId indicado",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping("/count")
    public ResponseEntity<CountSearchSessionResponse> findSearch(
            @Parameter(description = "ID de la sesión de búsqueda a consultar", example = "0114752", required = true)
            @RequestParam(name = "searchId") String searchId) {
        SearchSession searchSession = searchQueryUseCase.count(searchId);
        return ResponseEntity.ok(searchDomainMapper.toCountSearchSessionResponse(searchSession));
    }

}
