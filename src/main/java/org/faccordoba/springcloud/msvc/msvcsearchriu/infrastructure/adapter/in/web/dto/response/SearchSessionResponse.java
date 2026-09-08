package org.faccordoba.springcloud.msvc.msvcsearchriu.infrastructure.adapter.in.web.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta tras iniciar una sesión de búsqueda")
public record SearchSessionResponse(
        @Schema(description = "Identificador único asignado a la sesión de búsqueda", example = "a1b2c3d")
        String searchId
) {
}
