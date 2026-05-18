package com.ecommerce.orden_service.service;
import com.ecommerce.orden_service.client.CarritoClient;
import com.ecommerce.orden_service.dto.CarritoResponse;
import com.ecommerce.orden_service.dto.OrdenRequestDto;
import com.ecommerce.orden_service.dto.OrdenResponseDto;
import com.ecommerce.orden_service.exception.IdUsuarioNoEncontradoException;
import com.ecommerce.orden_service.exception.OrdenNoEncontradaException;
import com.ecommerce.orden_service.model.Orden;
import com.ecommerce.orden_service.repository.OrdenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j

public class OrdenService{

    @Autowired
    private OrdenRepository ordenRepository;

    @Autowired
    private CarritoClient carritoClient;

    // **** TOMA EL ID DEL USUARIO LOGGEADO
    private Long getUsuarioIdFromToken(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (Long) auth.getCredentials();
    }

    // **** TOMA EL EMAIL DEL USUARIO LOGEADO
    private String getEmailFromToken(){
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    // **** METODO PARA AUTORIZAR SI EL USUARIO INGRESADO TIENE ROL DE ADMIN
    private boolean esAdmin(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    // **** VERIFICACION DE CARRITO, CREACION Y MAPEO DE ORDEN
    public OrdenResponseDto crearOrden(OrdenRequestDto dto){

        String email = getEmailFromToken();
        Long usuarioId = getUsuarioIdFromToken();

        log.info("Creacion de orden para email {}", email);

        CarritoResponse carrito = carritoClient.obtenerCarritoPorUsuario(email);

        if (carrito == null || carrito.getItems() == null || carrito.getItems().isEmpty()) {
            log.warn("Carrito vacío o no encontrado para email {}", email);
            throw new RuntimeException("El carrito está vacío o no existe");
        }

        if (carrito.getTotal() == null || carrito.getTotal() <= 0) {
            log.warn("Total del carrito inválido: {}", carrito.getTotal());
            throw new RuntimeException("El carrito no tiene un total válido");
        }

        Orden orden = new Orden();
        orden.setUsuarioId(usuarioId);
        orden.setTotal(carrito.getTotal());
        orden.setEstado("PENDIENTE");
        orden.setFechaCreacion(LocalDateTime.now());
        Orden guardada = ordenRepository.save(orden);
        log.info("Orden creada con id {} para email {}", guardada.getId(), email);
        return mapToDTO(guardada);
    }

    // **** METODO PARA OBTENER TODAS LOS ORDENES
    public List<Orden> obtenerTodas() {
        if (esAdmin()) {
            return ordenRepository.findAll();
        }

        Long usuarioId = getUsuarioIdFromToken();
        return ordenRepository.findByUsuarioId(usuarioId);
    }

    // **** METODO PARA OBTENER ORDENES POR ID
    public Orden obtenerPorId(Long id) {
        Orden orden = ordenRepository.findById(id)
                .orElseThrow(() -> new OrdenNoEncontradaException("No existe orden con este id " + id));

        if (!esAdmin()) {
            Long usuarioId = getUsuarioIdFromToken();
            if (!orden.getUsuarioId().equals(usuarioId)){
                throw new OrdenNoEncontradaException("No existe orden con este id " + id);
            }
        }
        return orden;
    }

    //**** METODO PARA OBTENER ORDEN POR ID DE USUARIO
    public List<Orden> obtenerPorUsuario(Long usuarioId){
        if (!esAdmin()) {
            Long miId = getUsuarioIdFromToken();
            if (!miId.equals(usuarioId)) {
                throw new IdUsuarioNoEncontradoException("No existen órdenes para el usuario :" + usuarioId);
            }
        }

        List<Orden> ordenes = ordenRepository.findByUsuarioId(usuarioId);
        if (ordenes.isEmpty()) {
            throw new IdUsuarioNoEncontradoException("No existen órdenes para el usuario :" + usuarioId);
        }
        return ordenes;
    }

    // **** METODO PARA OBTENER ORDENES POR ESTADO (ADMIN)
    public List<Orden> obtenerPorEstado(String estado){
        if (esAdmin()) {
            return ordenRepository.findByEstado(estado);
        }
        Long usuarioId = getUsuarioIdFromToken();
        return ordenRepository.findByEstado(estado).stream()
                .filter(o -> o.getUsuarioId().equals(usuarioId))
                .toList();
    }

    // **** METODO PARA ACTUALIZAR ORDEN
    public Orden actualizarOrden(Long id, Orden nuevaOrden){
        Orden orden = obtenerPorId(id);
        orden.setUsuarioId(nuevaOrden.getUsuarioId());
        orden.setTotal(nuevaOrden.getTotal());
        orden.setFechaCreacion(nuevaOrden.getFechaCreacion());
        return ordenRepository.save(orden);
    }

    // **** METODO PARA ACTUALIZAR ESTADO DE ORDEN (PENDIENTE, PAGADO)
    public Orden actualizarEstado(Long id, String estado){

        Orden orden = obtenerPorId(id);
        if (!esAdmin()) {
            if (!orden.getEstado().equals("PENDIENTE")){
                throw new RuntimeException("Solo puedes cancelar órdenes en estado PENDIENTE");
            }
        }

        if (!estado.equals("PENDIENTE") &&
            !estado.equals("PAGADO")) {
            throw new RuntimeException("Estado inválido");
        }

        orden.setEstado(estado);
        return ordenRepository.save(orden);
    }

    // **** METODO PARA ELIMINAR ORDEN POR ID
    public void eliminarPorId(Long id) {
        Orden orden = obtenerPorId(id);
        ordenRepository.delete(orden);
    }

    // **** METODO PARA ELIMINAR ORDEN PO ESTADO (PENDIENTE O PAGADO)
    public void eliminarPorEstado(String estado){
        ordenRepository.deleteByEstado(estado);
    }

    // **** MAPEO DE ORDEN A DTO PARA RESPONSE
    private OrdenResponseDto mapToDTO(Orden orden){
        OrdenResponseDto dto = new OrdenResponseDto();
        dto.setId(orden.getId());
        dto.setUsuarioId(orden.getUsuarioId());
        dto.setTotal(orden.getTotal());
        dto.setEstado(orden.getEstado());
        dto.setFechaCreacion(orden.getFechaCreacion());
        return dto;
    }
}