package com.ecommerce.orden_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI(){
        return new OpenAPI()
                .info(new Info()
                        .title("Proyecto Libreria Ecommerce 2026")
                        .version("1.0")
                        .description("API para la gestión de órdenes de compra. Permite crear, consultar, actualizar y eliminar órdenes,así como filtrarlas por usuario, estado y fechas. Requiere autenticación mediante JWT con roles USUARIO o ADMIN."));
    }  
}
