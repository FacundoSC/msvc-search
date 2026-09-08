package org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.adapter.in.web.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta con el detalle y número de consultas de la búsqueda")
public record CountSearchSessionResponse(
        @Schema(description = "Identificador único de la sesión de búsqueda", example = "a1b2c3d")
        String searchId,

        @Schema(description = "Datos originales de la búsqueda")
        Payload search,

        @Schema(description = "Cantidad acumulada de visitas / consultas para esta búsqueda", example = "1")
        Integer count
) {
}
