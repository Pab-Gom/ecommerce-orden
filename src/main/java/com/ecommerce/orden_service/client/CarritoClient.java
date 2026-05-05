package com.ecommerce.orden_service.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class CarritoClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${carrito-service.url}")
    private String carritoServiceUrl;

    public CarritoClient(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public String obtenerCarritoPorUsuario(Long usuarioId) {
        try {
            return webClientBuilder.build()
                    .get()
                    .uri(carritoServiceUrl + "/usuario/{id}", usuarioId)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (Exception e) {
            return null;
        }
    }
}