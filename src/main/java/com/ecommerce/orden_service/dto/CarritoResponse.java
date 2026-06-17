package com.ecommerce.orden_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "DTO que representa la respuesta del microservicio de carrito")
public class CarritoResponse {

    @Schema(description = "ID del carrito", example = "1")
    private Integer id;

    @Schema(description = "Email del usuario propietario del carrito", example = "usuario@email.com")
    private String usuarioEmail;

    @Schema(description = "Items del carrito")
    private List<ItemCarritoResponse> items;

    @Schema(description = "Monto total del carrito", example = "150.00")
    private Double total;
}