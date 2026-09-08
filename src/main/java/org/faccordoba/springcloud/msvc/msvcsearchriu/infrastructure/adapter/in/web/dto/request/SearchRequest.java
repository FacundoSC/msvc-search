package org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.adapter.in.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.adapter.in.web.validator.FechaValida;

import java.util.List;

@Schema(description = "Solicitud para registrar una nueva búsqueda de hotel")
public record SearchRequest(
        @Schema(description = "Identificador único del hotel", example = "HOTEL001")
        @NotBlank(message = "{search-request.hotel-id.required}")
        String hotelId,

        @Schema(description = "Fecha de check-in (formato dd/MM/yyyy)", example = "01/10/2026")
        @NotBlank(message = "{search-request.checkin.required}")
        @FechaValida(message = "{search-request.checkin.date-valida}")
        String checkIn,

        @Schema(description = "Fecha de check-out (formato dd/MM/yyyy)", example = "05/10/2026")
        @NotBlank(message = "{search-request.checkout.required}")
        @FechaValida(message = "{search-request.checkout.date-valida}")
        String checkOut,

        @Schema(description = "Edades de los huéspedes", example = "[30, 28, 5]")
        @NotNull(message = "{search-request.ages.not-null}")
        @Size(min = 1, message = "{search-request.ages.min-size}")
        List<Integer> ages
) {
}
