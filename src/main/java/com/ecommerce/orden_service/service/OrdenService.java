package com.ecommerce.orden_service.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.orden_service.model.Orden;
import com.ecommerce.orden_service.repository.OrdenRepository;

import jakarta.validation.Valid;

import com.ecommerce.orden_service.dto.OrdenDto;
import com.ecommerce.orden_service.exception.OrdenNoEncontradaException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrdenService {

    @Autowired
    private OrdenRepository ordenRepository;


       // 🔹 CREAR ORDEN
    public OrdenDto crearOrden(OrdenDto dto) {

        log.info("Intentando crear orden para usuario {}", dto.getUsuarioId());

        // 🔥 1. Llamar al microservicio carrito
        String carrito = cartClient.obtenerCarritoPorUsuario(dto.getUsuarioId());

        // ❌ 2. Validar carrito vacío
        if (carrito == null || carrito.isEmpty()) {
            log.warn("Carrito vacío para usuario {}", dto.getUsuarioId());
            throw new RuntimeException("El carrito está vacío");
        }

        // ❌ 3. Validar total
        if (dto.getTotal() == null || dto.getTotal() <= 0) {
            log.warn("Total inválido");
            throw new RuntimeException("El total debe ser mayor a 0");
        }

        // ✅ 4. Crear entidad
        Orden orden = new Orden();
        orden.setUsuarioId(dto.getUsuarioId());
        orden.setTotal(dto.getTotal());


        // 🔥 reglas de negocio
        orden.setEstado("PENDIENTE");
        orden.setFechaCreacion(LocalDateTime.now());

        // 💾 guardar
        Orden guardada = ordenRepository.save(orden);

        log.info("Orden creada con id {}", guardada.getId());

        // 🔄 convertir a DTO
        return mapToDTO(guardada);
    }


    public List<Orden> obtenerTodas() {
        log.info("Listando órdenes");
        return ordenRepository.findAll();
    }

    public Orden obtenerPorId(Long id) {
        log.info("Buscando orden por id {}", id);
        return ordenRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Orden no encontrada por id {}", id);
                    return new OrdenNoEncontradaException("No existe orden con este id " + id);
                });
    }

    public List<Orden> obtenerPorUsuario(Long usuarioId) {
        log.info("Buscando órdenes del usuario {}", usuarioId);
        return ordenRepository.findByUsuarioId(usuarioId);
    }

    public List<Orden> obtenerPorEstado(String estado) {
        log.info("Buscando órdenes con estado {}", estado);
        return ordenRepository.findByEstado(estado);
    }

    public Orden actualizarOrden(Long id, Orden nuevaOrden) {
        log.info("Actualizando orden con id {}", id);

        Orden orden = obtenerPorId(id);

        orden.setUsuarioId(nuevaOrden.getUsuarioId());
        orden.setTotal(nuevaOrden.getTotal());
        orden.setFechaCreacion(nuevaOrden.getFechaCreacion());

        return ordenRepository.save(orden);
    }

    public Orden actualizarEstado(Long id, String estado) {
        log.info("Actualizando estado de orden con id {} a {}", id, estado);

        Orden orden = obtenerPorId(id);

        if (!estado.equals("PENDIENTE") &&
            !estado.equals("PAGADO") &&
            !estado.equals("ENVIADO")) {

            log.warn("Estado inválido: {}", estado);
            throw new RuntimeException("Estado inválido");
        }

        orden.setEstado(estado);

        return ordenRepository.save(orden);
    }

    public void eliminarPorId(Long id) {
        log.info("Eliminando orden por id {}", id);

        Orden orden = obtenerPorId(id);
        ordenRepository.delete(orden);
    }

    public void eliminarPorEstado(String estado) {
        log.info("Eliminando órdenes con estado {}", estado);
        ordenRepository.deleteByEstado(estado);
    }

    private OrdenDto mapToDTO(Orden orden) {

    OrdenDto dto = new OrdenDto();

    dto.setId(orden.getId());
    dto.setUsuarioId(orden.getUsuarioId());
    dto.setTotal(orden.getTotal());
    dto.setEstado(orden.getEstado());
    dto.setFechaCreacion(orden.getFechaCreacion());

    return dto;
    }
}