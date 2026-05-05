package com.ecommerce.orden_service.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class OrdenResponseDto {

    private Long id;
    private Long usuarioId;
    private Double total;
    private String estado;
    private LocalDateTime fechaCreacion;
}