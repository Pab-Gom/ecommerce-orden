package com.ecommerce.orden_service.dto;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO de respuesta con los datos de una orden creada")
public class OrdenResponseDto {

    @Schema(description = "ID único de la orden", example = "1")
    private Long id;

    @Schema(description = "ID del usuario propietario de la orden", example = "1")
    private Long usuarioId;

    @Schema(description = "Monto total de la orden", example = "150.00")
    private Double total;

    @Schema(description = "Estado de la orden (PENDIENTE, PAGADO, ENVIADO)", example = "PENDIENTE")
    private String estado;

    @Schema(description = "Fecha y hora de creación de la orden", example = "2025-06-16T10:30:00")
    private LocalDateTime fechaCreacion;

    @Schema(description = "Lista de items del carrito asociados a la orden")
    private List<ItemCarritoResponse> items;
}