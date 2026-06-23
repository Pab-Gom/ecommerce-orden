package com.ecommerce.orden_service.controller;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ecommerce.orden_service.dto.OrdenRequestDto;
import com.ecommerce.orden_service.dto.OrdenResponseDto;
import com.ecommerce.orden_service.model.Orden;
import com.ecommerce.orden_service.service.OrdenService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@RequestMapping("/ordenes")
@Tag(name = "Órdenes", description = "API de gestión de órdenes de compra")
public class OrdenController {
    
    @Autowired
    private OrdenService ordenService;

    // ****CREAR ORDEN
    @PostMapping
    @Operation(summary = "Crear una nueva orden",
               description = "Crea una orden a partir del carrito del usuario autenticado. El usuarioId y total se obtienen automáticamente del JWT y del carrito respectivamente.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Orden creada exitosamente",
                     content = @Content(schema = @Schema(implementation = OrdenResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Carrito vacío o datos inválidos"),
        @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<OrdenResponseDto> crearOrden(
        @RequestBody OrdenRequestDto dto) {
    return ResponseEntity.ok(ordenService.crearOrden(dto));
    }

    // ****OBTENER TODAS
    @GetMapping
    @Operation(summary = "Obtener todas las órdenes",
               description = "Obtiene todas las órdenes. Los ADMIN ven todas y los USUARIO solo las suyas.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de órdenes obtenida",
                     content = @Content(schema = @Schema(implementation = Orden.class))),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado - se requiere rol ADMIN")
    })
    public ResponseEntity<List<Orden>> obtenerTodas(){
        return ResponseEntity.ok(ordenService.obtenerTodas());
    }

    // ****OBTENER POR ID
    @GetMapping("/{id}")
    @Operation(summary = "Obtener orden por ID",
               description = "Obtiene una orden específica por su ID. Si no es ADMIN, solo puede ver sus propias órdenes.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Orden encontrada",
                     content = @Content(schema = @Schema(implementation = Orden.class))),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "404", description = "Orden no encontrada")
    })
    public ResponseEntity<Orden> obtenerPorId(@Parameter(description = "ID de la orden", required = true)@PathVariable Long id){
        return ResponseEntity.ok(ordenService.obtenerPorId(id));
    }

    // ****BUSCAR POR USUARIO
    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Obtener órdenes por usuario",
               description = "Obtiene todas las órdenes de un usuario específico. Solo accesible para ADMIN.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Órdenes del usuario encontradas",
                     content = @Content(schema = @Schema(implementation = Orden.class))),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado - se requiere rol ADMIN"),
        @ApiResponse(responseCode = "404", description = "Usuario sin órdenes")})
    public ResponseEntity<List<Orden>> obtenerPorUsuario(@Parameter(description = "ID del usuario", required = true)@PathVariable Long usuarioId){
        return ResponseEntity.ok(ordenService.obtenerPorUsuario(usuarioId));
    }

    // ****BUSCAR POR ESTADO
    @GetMapping("/estado/{estado}")
     @Operation(summary = "Obtener órdenes por estado",
               description = "Filtra órdenes por estado (PENDIENTE, PAGADO, ENVIADO). " +
                             "ADMIN ve todas; USUARIO solo las suyas.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Órdenes filtradas",
                     content = @Content(schema = @Schema(implementation = Orden.class))),
        @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<List<Orden>> obtenerPorEstado(@Parameter(description = "Estado de la orden (PENDIENTE, PAGADO, ENVIADO)", required = true, example = "PENDIENTE")@PathVariable String estado){
        return ResponseEntity.ok(ordenService.obtenerPorEstado(estado));
    }

    // ****ACTUALIZAR ORDEN COMPLETA
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar orden completa",
               description = "Actualiza todos los campos de una orden. Solo accesible para ADMIN.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Orden actualizada",
                     content = @Content(schema = @Schema(implementation = Orden.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado - se requiere rol ADMIN"),
        @ApiResponse(responseCode = "404", description = "Orden no encontrada")
    })
    public ResponseEntity<Orden> actualizarOrden(@Parameter(description = "ID de la orden", required = true)@PathVariable Long id,@RequestBody Orden orden){
        return ResponseEntity.ok(ordenService.actualizarOrden(id, orden));
    }

    // ****ACTUALIZAR SOLO ESTADO
    @PatchMapping("/{id}/estado")
    @Operation(summary = "Actualizar estado de una orden",
               description = "Actualiza solo el estado de una orden. " +
                             "Posibles valores: PENDIENTE, PAGADO, ENVIADO." +
                             "ADMIN puede cambiar a cualquier estado; USUARIO solo puede cancelar si está PENDIENTE.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estado actualizado",
                     content = @Content(schema = @Schema(implementation = Orden.class))),
        @ApiResponse(responseCode = "400", description = "Estado inválido o transición no permitida"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado"),
        @ApiResponse(responseCode = "404", description = "Orden no encontrada")
    })
    public ResponseEntity<Orden> actualizarEstado(@Parameter(description = "ID de la orden", required = true)
            @PathVariable Long id, @Parameter(description = "Nuevo estado (PENDIENTE, PAGADO, ENVIADO)", required = true)
            @RequestParam String estado){
      return ResponseEntity.ok(ordenService.actualizarEstado(id, estado));
    }

    // ****ELIMINAR POR ID
    @DeleteMapping("/{id}")
     @Operation(summary = "Eliminar orden por ID",
               description = "Elimina una orden específica. Solo accesible para ADMIN.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Orden eliminada exitosamente"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado - se requiere rol ADMIN"),
        @ApiResponse(responseCode = "404", description = "Orden no encontrada")
    })
    public ResponseEntity<Void> eliminarPorId(@Parameter(description = "ID de la orden a eliminar", required = true)
        @PathVariable Long id){
        ordenService.eliminarPorId(id);
        return ResponseEntity.noContent().build();
    }

    // ****ELIMINAR POR ESTADO
    @DeleteMapping("/estado/{estado}")
    @Operation(summary = "Eliminar órdenes por estado",
               description = "Elimina todas las órdenes con un estado específico. Solo accesible para ADMIN.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Órdenes eliminadas exitosamente"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado - se requiere rol ADMIN")
    })
    public ResponseEntity<Void> eliminarPorEstado(@Parameter(description = "Estado de las órdenes a eliminar", required = true)
        @PathVariable String estado){
        ordenService.eliminarPorEstado(estado);
        return ResponseEntity.noContent().build();
    }

    // ****VER ORDENES DEL USUARIO
    @GetMapping("/mis-ordenes")
    @Operation(summary = "Obtener mis órdenes",
               description = "Obtiene las órdenes del usuario autenticado.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Órdenes del usuario autenticado",
                     content = @Content(schema = @Schema(implementation = Orden.class))),
        @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<List<Orden>> obtenerMisOrdenes() {
        return ResponseEntity.ok(ordenService.obtenerMisOrdenes());
    }

    // ****ELIMINAR ORDENES DEL USUARIO POR ID
    @DeleteMapping("/mis-ordenes/{id}")
     @Operation(summary = "Eliminar mi orden por ID",
               description = "Elimina una de las órdenes del usuario autenticado.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Orden eliminada exitosamente"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "404", description = "Orden no encontrada")
    })
    public ResponseEntity<Void> eliminarPorIdUsuario(@Parameter(description = "ID de la orden a eliminar", required = true)
        @PathVariable Long id){
        ordenService.eliminarPorId(id);
        return ResponseEntity.noContent().build();
    }
}