package com.ecommerce.orden_service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ecommerce.orden_service.model.Orden;
import com.ecommerce.orden_service.service.OrdenService;

@RestController
@RequestMapping("/ordenes")
public class OrdenController {

    private final OrdenService ordenService;

    public OrdenController(OrdenService ordenService) {
        this.ordenService = ordenService;
    }

    // 🔹 CREAR ORDEN
    @PostMapping
    public ResponseEntity<Orden> crearOrden(@RequestBody Orden orden) {
        return ResponseEntity.ok(ordenService.crearOrden(orden));
    }

    // 🔹 OBTENER TODAS
    @GetMapping
    public ResponseEntity<List<Orden>> obtenerTodas() {
        return ResponseEntity.ok(ordenService.obtenerTodas());
    }

    // 🔹 OBTENER POR ID
    @GetMapping("/{id}")
    public ResponseEntity<Orden> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ordenService.obtenerPorId(id));
    }

    // 🔹 BUSCAR POR USUARIO
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Orden>> obtenerPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(ordenService.obtenerPorUsuario(usuarioId));
    }

    // 🔹 BUSCAR POR ESTADO
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Orden>> obtenerPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(ordenService.obtenerPorEstado(estado));
    }

    // 🔹 ACTUALIZAR ORDEN COMPLETA
    @PutMapping("/{id}")
    public ResponseEntity<Orden> actualizarOrden(
            @PathVariable Long id,
            @RequestBody Orden orden) {

        return ResponseEntity.ok(ordenService.actualizarOrden(id, orden));
    }

    // 🔹 ACTUALIZAR SOLO ESTADO
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Orden> actualizarEstado(
            @PathVariable Long id,
            @RequestParam String estado) {

        return ResponseEntity.ok(ordenService.actualizarEstado(id, estado));
    }

    // 🔹 ELIMINAR POR ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPorId(@PathVariable Long id) {
        ordenService.eliminarPorId(id);
        return ResponseEntity.noContent().build();
    }

    // 🔹 ELIMINAR POR ESTADO
    @DeleteMapping("/estado/{estado}")
    public ResponseEntity<Void> eliminarPorEstado(@PathVariable String estado) {
        ordenService.eliminarPorEstado(estado);
        return ResponseEntity.noContent().build();
    }
}