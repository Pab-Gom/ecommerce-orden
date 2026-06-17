package com.ecommerce.orden_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.ecommerce.orden_service.client.CarritoClient;
import com.ecommerce.orden_service.dto.CarritoResponse;
import com.ecommerce.orden_service.dto.ItemCarritoResponse;
import com.ecommerce.orden_service.dto.OrdenRequestDto;
import com.ecommerce.orden_service.dto.OrdenResponseDto;
import com.ecommerce.orden_service.model.Orden;
import com.ecommerce.orden_service.repository.OrdenRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class OrdenServiceTest {

    @Mock
    private OrdenRepository ordenRepository;

    @Mock
    private CarritoClient carritoClient;

    @InjectMocks
    private OrdenService ordenService;

    @BeforeEach
    void setup() {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                        "admin@email.com",
                        1L,
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void deberiaCrearOrden() {
        OrdenRequestDto request = new OrdenRequestDto();

        ItemCarritoResponse item = new ItemCarritoResponse();
        item.setId(1);
        item.setProductoId(101);
        item.setNombreProducto("Libro de Java");
        item.setPrecioUnitario(50000.0);
        item.setCantidad(1);

        CarritoResponse carrito = new CarritoResponse();
        carrito.setId(1);
        carrito.setUsuarioEmail("admin@email.com");
        carrito.setItems(List.of(item));
        carrito.setTotal(50000.0);

        Orden ordenGuardada = new Orden();
        ordenGuardada.setId(100L);
        ordenGuardada.setUsuarioId(1L);
        ordenGuardada.setTotal(50000.0);
        ordenGuardada.setEstado("PENDIENTE");
        ordenGuardada.setFechaCreacion(LocalDateTime.now());

        when(carritoClient.obtenerCarritoPorUsuario("admin@email.com"))
                .thenReturn(carrito);

        when(ordenRepository.save(any(Orden.class)))
                .thenReturn(ordenGuardada);

        OrdenResponseDto resultado = ordenService.crearOrden(request);

        assertNotNull(resultado);
        assertEquals("PENDIENTE", resultado.getEstado());
        assertEquals(50000.0, resultado.getTotal());

        verify(ordenRepository).save(any(Orden.class));
    }

    @Test
    void deberiaLanzarErrorSiCarritoVacio() {
        OrdenRequestDto request = new OrdenRequestDto();

        when(carritoClient.obtenerCarritoPorUsuario("admin@email.com"))
                .thenReturn(null);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> ordenService.crearOrden(request)
        );

        assertEquals("El carrito está vacío o no existe", exception.getMessage());
    }

    @Test
    void deberiaObtenerOrdenPorId() {
        Orden orden = new Orden();
        orden.setId(1L);
        orden.setUsuarioId(10L);
        orden.setTotal(50000.0);
        orden.setEstado("PENDIENTE");
        orden.setFechaCreacion(LocalDateTime.now());

        when(ordenRepository.findById(1L))
                .thenReturn(Optional.of(orden));

        Orden resultado = ordenService.obtenerPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());

        verify(ordenRepository).findById(1L);
    }

    @Test
    void deberiaActualizarEstadoOrden() {
        Orden orden = new Orden();
        orden.setId(1L);
        orden.setUsuarioId(10L);
        orden.setTotal(50000.0);
        orden.setEstado("PENDIENTE");
        orden.setFechaCreacion(LocalDateTime.now());

        when(ordenRepository.findById(1L))
                .thenReturn(Optional.of(orden));

        when(ordenRepository.save(any(Orden.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Orden resultado = ordenService.actualizarEstado(1L, "PAGADO");

        assertEquals("PAGADO", resultado.getEstado());

        verify(ordenRepository).save(any(Orden.class));
    }
}