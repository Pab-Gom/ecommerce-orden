package com.ecommerce.orden_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.orden_service.model.Orden;
import java.util.List;
import java.time.LocalDateTime;
public interface OrdenRepository extends JpaRepository<Orden, Long>{

    List<Orden> findByEstado(String estado);

    List<Orden> findByUsuarioId(Long usuarioId);

    List<Orden> findByFechaCreacion(LocalDateTime fechaCreacion);

    void deleteByEstado(String estado);
    
}
