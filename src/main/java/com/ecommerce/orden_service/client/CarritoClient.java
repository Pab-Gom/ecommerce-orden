package com.ecommerce.orden_service.client;
import com.ecommerce.orden_service.dto.CarritoResponse;
import com.ecommerce.orden_service.util.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component

public class CarritoClient{

    private final WebClient webClient;
    private final JwtUtil jwtUtil;
    public CarritoClient(WebClient.Builder webClientBuilder,
                         JwtUtil jwtUtil,
                         @Value("${carrito-service.url}") String carritoServiceUrl) {
        this.jwtUtil = jwtUtil;
        this.webClient = webClientBuilder.baseUrl(carritoServiceUrl).build();
    }

    public CarritoResponse obtenerCarritoPorUsuario(String email){
        
        try {
            String token = jwtUtil.generateInternalToken(email);
            return webClient.get()
                    .uri("")
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .bodyToMono(CarritoResponse.class)
                    .block();
        } catch (Exception e) {
            return null;
        }
    }
}