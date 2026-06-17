package com.ecommerce.orden_service.model;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name= "orden")
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad que representa una orden de compra")

public class Orden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único de la orden")
    private Long id;

    @Column(nullable = false)
    @Schema(description = "ID del usuario que realizó la orden")
    private Long usuarioId;

    @Column(nullable = false)
    @Schema(description = "Monto total de la orden")
    private Double total;

    @Column(nullable = false, length = 20)
    @Schema(description = "Estado de la orden (PENDIENTE, PAGADO, ENVIADO)")
    private String estado;

    @Column(nullable = false)
    @Schema(description = "Fecha y hora de creación de la orden")
    private LocalDateTime fechaCreacion;
    
}
