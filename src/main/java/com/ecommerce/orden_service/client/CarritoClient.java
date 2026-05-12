package com.ecommerce.orden_service.client;

import org.springframework.stereotype.Component;

@Component
public class CarritoClient {

    //****SIMULACION CARRITO DE PRUEBA
    public String obtenerCarritoPorUsuario(Long usuarioId) {

        return "CARRITO_OK";

    }
}