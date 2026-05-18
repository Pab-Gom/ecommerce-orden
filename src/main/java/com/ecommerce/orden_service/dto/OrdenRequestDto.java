package com.ecommerce.orden_service.dto;

import jakarta.validation.constraints.*;

import lombok.Data;

@Data
public class OrdenRequestDto {

    @NotNull(message = "El usuarioId es obligatorio")
    private Long usuarioId;

}