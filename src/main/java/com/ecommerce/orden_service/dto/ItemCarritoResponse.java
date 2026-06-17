package com.ecommerce.orden_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO que representa un item dentro del carrito")
public class ItemCarritoResponse {

    @Schema(description = "ID del item en el carrito", example = "1")
    private Integer id;

    @Schema(description = "ID del producto", example = "101")
    private Integer productoId;

    @Schema(description = "Nombre del producto", example = "Libro de Java")
    private String nombreProducto;

    @Schema(description = "Precio unitario del producto", example = "25.50")
    private Double precioUnitario;

    @Schema(description = "Cantidad seleccionada del producto", example = "2")
    private Integer cantidad;
}