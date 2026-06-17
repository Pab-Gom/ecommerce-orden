package com.ecommerce.orden_service.exception;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO estándar para respuestas de error")

public class ErrorResponse {

    @Schema(description = "Mensaje descriptivo del error")
    private String mensaje;

    @Schema(description = "Código de estado HTTP")
    private int status;

    @Schema(description = "Timestamp del error")
    private LocalDateTime timestamp;
}