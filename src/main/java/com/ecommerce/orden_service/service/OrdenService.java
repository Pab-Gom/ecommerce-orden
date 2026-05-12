package com.ecommerce.orden_service.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.orden_service.client.CarritoClient;
import com.ecommerce.orden_service.dto.OrdenRequestDto;
import com.ecommerce.orden_service.dto.OrdenResponseDto;
import com.ecommerce.orden_service.exception.IdUsuarioNoEncontradoException;
import com.ecommerce.orden_service.exception.OrdenNoEncontradaException;
import com.ecommerce.orden_service.model.Orden;
import com.ecommerce.orden_service.repository.OrdenRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrdenService {

    @Autowired
    private OrdenRepository ordenRepository;

    @Autowired
    private CarritoClient carritoClient;

    //****CREAR ORDEN
    public OrdenResponseDto crearOrden(OrdenRequestDto dto){

        log.info("Creacion de orden para usuario {}", dto.getUsuarioId());

        String carrito = carritoClient.obtenerCarritoPorUsuario(dto.getUsuarioId());

        if (carrito == null || carrito.isEmpty()) {
            log.warn("Carrito vacío para usuario {}", dto.getUsuarioId());
            throw new RuntimeException("El carrito está vacío");
        }

        if (dto.getTotal() == null || dto.getTotal() <= 0) {
            log.warn("Total inválido");
            throw new RuntimeException("El total debe ser mayor a 0");
        }

        Orden orden = new Orden();
        orden.setUsuarioId(dto.getUsuarioId());
        orden.setTotal(dto.getTotal());
        orden.setEstado("PENDIENTE");
        orden.setFechaCreacion(LocalDateTime.now());

        Orden guardada = ordenRepository.save(orden);

        log.info("Orden creada con id {}", guardada.getId());

        return mapToDTO(guardada);
    }

    public List<Orden> obtenerTodas() {
        return ordenRepository.findAll();
    }

    public Orden obtenerPorId(Long id) {
        return ordenRepository.findById(id)
                .orElseThrow(()-> new OrdenNoEncontradaException("No existe orden con este id " + id));
    }

    public List<Orden> obtenerPorUsuario(Long usuarioId) {

    List<Orden> ordenes = ordenRepository.findByUsuarioId(usuarioId);
    if (ordenes.isEmpty()) {
        throw new IdUsuarioNoEncontradoException("No existen órdenes para el usuario :" + usuarioId);
        }

    return ordenes;
    }

    public List<Orden> obtenerPorEstado(String estado) {
        return ordenRepository.findByEstado(estado);
    }

    public Orden actualizarOrden(Long id, Orden nuevaOrden) {

        Orden orden = obtenerPorId(id);

        orden.setUsuarioId(nuevaOrden.getUsuarioId());
        orden.setTotal(nuevaOrden.getTotal());
        orden.setFechaCreacion(nuevaOrden.getFechaCreacion());

        return ordenRepository.save(orden);
    }

    public Orden actualizarEstado(Long id, String estado) {

        Orden orden = obtenerPorId(id);

        if (!estado.equals("PENDIENTE") &&
            !estado.equals("PAGADO") &&
            !estado.equals("ENVIADO")) {

            throw new RuntimeException("Estado inválido");
        }

        orden.setEstado(estado);

        return ordenRepository.save(orden);
    }

    public void eliminarPorId(Long id) {
        Orden orden = obtenerPorId(id);
        ordenRepository.delete(orden);
    }

    public void eliminarPorEstado(String estado) {
        ordenRepository.deleteByEstado(estado);
    }

    private OrdenResponseDto mapToDTO(Orden orden) {

        OrdenResponseDto dto = new OrdenResponseDto();

        dto.setId(orden.getId());
        dto.setUsuarioId(orden.getUsuarioId());
        dto.setTotal(orden.getTotal());
        dto.setEstado(orden.getEstado());
        dto.setFechaCreacion(orden.getFechaCreacion());

        return dto;
    }
}