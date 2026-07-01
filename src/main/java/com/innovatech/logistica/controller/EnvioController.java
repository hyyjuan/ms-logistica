package com.innovatech.logistica.controller;

import com.innovatech.logistica.dto.ActualizarEstadoDTO;
import com.innovatech.logistica.dto.EnvioRequestDTO;
import com.innovatech.logistica.dto.EnvioResponseDTO;
import com.innovatech.logistica.service.EnvioService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/logistica")
@RequiredArgsConstructor
@Validated
public class EnvioController {

    private final EnvioService envioService;

    @PostMapping
    public ResponseEntity<EnvioResponseDTO> crearEnvio(@Valid @RequestBody EnvioRequestDTO request) {
        EnvioResponseDTO response = envioService.crearEnvio(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/envios")
    public ResponseEntity<EnvioResponseDTO> crearEnvioAlias(@Valid @RequestBody EnvioRequestDTO request) {
        return crearEnvio(request);
    }

    @GetMapping
    public ResponseEntity<List<EnvioResponseDTO>> listarEnvios(@RequestParam(required = false) Long usuarioId) {
        if (usuarioId != null) {
            return ResponseEntity.ok(envioService.listarEnviosPorUsuario(usuarioId));
        }
        return ResponseEntity.ok(envioService.listarEnvios());
    }

    @GetMapping("/envios")
    public ResponseEntity<List<EnvioResponseDTO>> listarEnviosAlias(@RequestParam(required = false) Long usuarioId) {
        return listarEnvios(usuarioId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnvioResponseDTO> obtenerPorId(
            @PathVariable @Positive(message = "El id debe ser mayor a cero") Long id) {
        return ResponseEntity.ok(envioService.obtenerPorId(id));
    }

    @GetMapping("/envios/{id}")
    public ResponseEntity<EnvioResponseDTO> obtenerPorIdAlias(
            @PathVariable @Positive(message = "El id debe ser mayor a cero") Long id) {
        return obtenerPorId(id);
    }

    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<EnvioResponseDTO> obtenerPorPedidoId(
            @PathVariable @Positive(message = "El pedidoId debe ser mayor a cero") Long pedidoId) {
        return ResponseEntity.ok(envioService.obtenerPorPedidoId(pedidoId));
    }

    @GetMapping("/envios/pedido/{pedidoId}")
    public ResponseEntity<EnvioResponseDTO> obtenerPorPedidoIdAlias(
            @PathVariable @Positive(message = "El pedidoId debe ser mayor a cero") Long pedidoId) {
        return obtenerPorPedidoId(pedidoId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EnvioResponseDTO> actualizarEnvio(
            @PathVariable @Positive(message = "El id debe ser mayor a cero") Long id,
            @Valid @RequestBody EnvioRequestDTO request) {
        return ResponseEntity.ok(envioService.actualizarEnvio(id, request));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<EnvioResponseDTO> actualizarEstado(
            @PathVariable @Positive(message = "El id debe ser mayor a cero") Long id,
            @Valid @RequestBody ActualizarEstadoDTO dto) {
        return ResponseEntity.ok(envioService.actualizarEstado(id, dto));
    }

    @PatchMapping("/envios/{id}/estado")
    public ResponseEntity<EnvioResponseDTO> actualizarEstadoAlias(
            @PathVariable @Positive(message = "El id debe ser mayor a cero") Long id,
            @Valid @RequestBody ActualizarEstadoDTO dto) {
        return actualizarEstado(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarEnvio(
            @PathVariable @Positive(message = "El id debe ser mayor a cero") Long id) {
        envioService.eliminarEnvio(id);
        return ResponseEntity.noContent().build();
    }
}
