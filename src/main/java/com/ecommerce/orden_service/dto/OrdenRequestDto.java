package com.ecommerce.orden_service.dto;

import lombok.Data;

@Data
public class OrdenRequestDto {

    private Long usuarioId;
    private Double total;

}