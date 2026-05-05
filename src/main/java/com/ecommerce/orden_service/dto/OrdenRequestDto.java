package com.ecommerce.orden_service.dto;

import jakarta.validation.constraints.*;

import lombok.Data;

@Data
public class OrdenRequestDto {

    @NotNull(message = "El usuarioId es obligatorio")
    private Long usuarioId;

    @NotNull(message = "El total es obligatorio")
    @Positive(message = "El total debe ser mayor a 0")
    private Double total;

}