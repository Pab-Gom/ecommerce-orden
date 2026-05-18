package com.ecommerce.orden_service.dto;
import lombok.Data;

@Data

public class ItemCarritoResponse {
    private Integer id;
    private Integer productoId;
    private String nombreProducto;
    private Double precioUnitario;
    private Integer cantidad;
}