package com.ecommerce.orden_service.dto;
import lombok.Data;
import java.util.List;

@Data

public class CarritoResponse{

    private Integer id;
    private String usuarioEmail;
    private List<ItemCarritoResponse> items;
    private Double total;
}
