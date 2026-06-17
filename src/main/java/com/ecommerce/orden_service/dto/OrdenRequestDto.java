package com.ecommerce.orden_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO de solicitud para crear una orden")
public class OrdenRequestDto {

    //** DATOS INNECESARIOS */
    @Schema(description = "ID del usuario (se obtiene automáticamente del JWT)", example = "1", hidden = true)
    private Long usuarioId;
    
    @Schema(description = "Total de la orden (se obtiene automáticamente del carrito)", example = "150.00", hidden = true)
    private Double total;

}