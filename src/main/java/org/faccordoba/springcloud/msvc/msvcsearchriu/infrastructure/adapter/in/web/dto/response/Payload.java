package org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.adapter.in.web.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Detalle de los parámetros de búsqueda registrados")
public record Payload(
                @Schema(description = "Identificador del hotel", example = "HOTEL001") String hotelId,

                @Schema(description = "Fecha de check-in (dd/MM/yyyy)", example = "01/10/2026") String checkIn,

                @Schema(description = "Fecha de check-out (dd/MM/yyyy)", example = "05/10/2026") String checkOut,

                @Schema(description = "Edades de los huéspedes", example = "[30, 28, 5]") List<Integer> ages) {
}
