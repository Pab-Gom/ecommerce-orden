package com.ecommerce.orden_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ecommerce.orden_service.assembler.OrdenModelAssembler;
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
@Tag(name = "Órdenes V2", description = "API de gestión de órdenes de compra con HATEOAS")
public class OrdenControllerV2 {

    @Autowired
    private OrdenService ordenService;

    @Autowired
    private OrdenModelAssembler assembler;

    // ****CREAR ORDEN
    @PostMapping
    @Operation(summary = "Crear una nueva orden",
               description = "Crea una orden a partir del carrito del usuario autenticado.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Orden creada exitosamente",
                     content = @Content(schema = @Schema(implementation = OrdenResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Carrito vacío o datos inválidos"),
        @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<EntityModel<OrdenResponseDto>> crearOrden(@RequestBody OrdenRequestDto dto) {
        OrdenResponseDto dtoResponse = ordenService.crearOrden(dto);
        Orden orden = ordenService.obtenerPorId(dtoResponse.getId());
        return ResponseEntity.ok(assembler.toModel(orden));
    }

    // ****OBTENER TODAS
    @GetMapping
    @Operation(summary = "Obtener todas las órdenes",
               description = "Obtiene todas las órdenes. Los ADMIN ven todas y los USUARIO solo las suyas.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de órdenes obtenida"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado - se requiere rol ADMIN")
    })
    public ResponseEntity<CollectionModel<EntityModel<OrdenResponseDto>>> obtenerTodas() {
        List<Orden> ordenes = ordenService.obtenerTodas();
        return ResponseEntity.ok(assembler.toCollectionModel(ordenes));
    }

    // ****OBTENER POR ID
    @GetMapping("/{id}")
    @Operation(summary = "Obtener orden por ID",
               description = "Obtiene una orden específica por su ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Orden encontrada"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "404", description = "Orden no encontrada")
    })
    public ResponseEntity<EntityModel<OrdenResponseDto>> obtenerPorId(
            @Parameter(description = "ID de la orden", required = true) @PathVariable Long id) {
        Orden orden = ordenService.obtenerPorId(id);
        return ResponseEntity.ok(assembler.toModel(orden));
    }

    // ****BUSCAR POR USUARIO
    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Obtener órdenes por usuario",
               description = "Obtiene todas las órdenes de un usuario específico. Solo accesible para ADMIN.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Órdenes del usuario encontradas"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado - se requiere rol ADMIN"),
        @ApiResponse(responseCode = "404", description = "Usuario sin órdenes")
    })
    public ResponseEntity<CollectionModel<EntityModel<OrdenResponseDto>>> obtenerPorUsuario(
            @Parameter(description = "ID del usuario", required = true) @PathVariable Long usuarioId) {
        List<Orden> ordenes = ordenService.obtenerPorUsuario(usuarioId);
        return ResponseEntity.ok(assembler.toCollectionModel(ordenes));
    }

    // ****BUSCAR POR ESTADO
    @GetMapping("/estado/{estado}")
    @Operation(summary = "Obtener órdenes por estado",
               description = "Filtra órdenes por estado (PENDIENTE, PAGADO, ENVIADO).")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Órdenes filtradas"),
        @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<CollectionModel<EntityModel<OrdenResponseDto>>> obtenerPorEstado(
            @Parameter(description = "Estado de la orden", required = true, example = "PENDIENTE") @PathVariable String estado) {
        List<Orden> ordenes = ordenService.obtenerPorEstado(estado);
        return ResponseEntity.ok(assembler.toCollectionModel(ordenes));
    }

    // ****ACTUALIZAR ORDEN COMPLETA
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar orden completa",
               description = "Actualiza todos los campos de una orden. Solo accesible para ADMIN.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Orden actualizada"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado - se requiere rol ADMIN"),
        @ApiResponse(responseCode = "404", description = "Orden no encontrada")
    })
    public ResponseEntity<EntityModel<OrdenResponseDto>> actualizarOrden(
            @Parameter(description = "ID de la orden", required = true) @PathVariable Long id,
            @RequestBody Orden orden) {
        Orden actualizada = ordenService.actualizarOrden(id, orden);
        return ResponseEntity.ok(assembler.toModel(actualizada));
    }

    // ****ACTUALIZAR SOLO ESTADO
    @PatchMapping("/{id}/estado")
    @Operation(summary = "Actualizar estado de una orden",
               description = "Actualiza solo el estado de una orden.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estado actualizado"),
        @ApiResponse(responseCode = "400", description = "Estado inválido o transición no permitida"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado"),
        @ApiResponse(responseCode = "404", description = "Orden no encontrada")
    })
    public ResponseEntity<EntityModel<OrdenResponseDto>> actualizarEstado(
            @Parameter(description = "ID de la orden", required = true) @PathVariable Long id,
            @Parameter(description = "Nuevo estado (PENDIENTE, PAGADO, ENVIADO)", required = true) @RequestParam String estado) {
        Orden actualizada = ordenService.actualizarEstado(id, estado);
        return ResponseEntity.ok(assembler.toModel(actualizada));
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
    public ResponseEntity<Void> eliminarPorId(
            @Parameter(description = "ID de la orden a eliminar", required = true) @PathVariable Long id) {
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
    public ResponseEntity<Void> eliminarPorEstado(
            @Parameter(description = "Estado de las órdenes a eliminar", required = true) @PathVariable String estado) {
        ordenService.eliminarPorEstado(estado);
        return ResponseEntity.noContent().build();
    }

    // ****VER ORDENES DEL USUARIO
    @GetMapping("/mis-ordenes")
    @Operation(summary = "Obtener mis órdenes",
               description = "Obtiene las órdenes del usuario autenticado.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Órdenes del usuario autenticado"),
        @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<CollectionModel<EntityModel<OrdenResponseDto>>> obtenerMisOrdenes() {
        List<Orden> ordenes = ordenService.obtenerTodas();
        return ResponseEntity.ok(assembler.toCollectionModel(ordenes));
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
    public ResponseEntity<Void> eliminarPorIdUsuario(
            @Parameter(description = "ID de la orden a eliminar", required = true) @PathVariable Long id) {
        ordenService.eliminarPorId(id);
        return ResponseEntity.noContent().build();
    }
}