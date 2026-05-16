package com.innovatech.logistica.controller;

import com.innovatech.logistica.dto.ActualizarEstadoDTO;
import com.innovatech.logistica.dto.EnvioRequestDTO;
import com.innovatech.logistica.dto.EnvioResponseDTO;
import com.innovatech.logistica.service.EnvioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/logistica")
@RequiredArgsConstructor
public class EnvioController {

    private final EnvioService envioService;

    // ── POST /api/v1/logistica ─────────────────────────────────────
    // Creación manual de un envío (útil para admin o pruebas)
    @PostMapping
    public ResponseEntity<EnvioResponseDTO> crearEnvio(
            @Valid @RequestBody EnvioRequestDTO request) {
        EnvioResponseDTO response = envioService.crearEnvio(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ── GET /api/v1/logistica ──────────────────────────────────────
    // Lista todos los envíos (uso admin / dashboard)
    @GetMapping
    public ResponseEntity<List<EnvioResponseDTO>> listarEnvios() {
        return ResponseEntity.ok(envioService.listarEnvios());
    }

    // ── GET /api/v1/logistica/{id} ─────────────────────────────────
    // Obtiene un envío por su ID interno
    @GetMapping("/{id}")
    public ResponseEntity<EnvioResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(envioService.obtenerPorId(id));
    }

    // ── GET /api/v1/logistica/pedido/{pedidoId} ───────────────────
    // El endpoint más usado por el cliente: tracking por pedido
    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<EnvioResponseDTO> obtenerPorPedidoId(@PathVariable Long pedidoId) {
        return ResponseEntity.ok(envioService.obtenerPorPedidoId(pedidoId));
    }

    // ── PUT /api/v1/logistica/{id} ────────────────────────────────
    // Actualiza datos del envío (dirección, contacto, etc.)
    @PutMapping("/{id}")
    public ResponseEntity<EnvioResponseDTO> actualizarEnvio(
            @PathVariable Long id,
            @Valid @RequestBody EnvioRequestDTO request) {
        return ResponseEntity.ok(envioService.actualizarEnvio(id, request));
    }

    // ── PATCH /api/v1/logistica/{id}/estado ───────────────────────
    // Cambia el estado del envío: el corazón del tracking
    @PatchMapping("/{id}/estado")
    public ResponseEntity<EnvioResponseDTO> actualizarEstado(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarEstadoDTO dto) {
        return ResponseEntity.ok(envioService.actualizarEstado(id, dto));
    }

    // ── DELETE /api/v1/logistica/{id} ─────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarEnvio(@PathVariable Long id) {
        envioService.eliminarEnvio(id);
        return ResponseEntity.noContent().build();
    }
}
