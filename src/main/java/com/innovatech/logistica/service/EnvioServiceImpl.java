package com.innovatech.logistica.service;

import com.innovatech.logistica.dto.ActualizarEstadoDTO;
import com.innovatech.logistica.dto.EnvioRequestDTO;
import com.innovatech.logistica.dto.EnvioResponseDTO;
import com.innovatech.logistica.exception.EnvioNotFoundException;
import com.innovatech.logistica.exception.EnvioYaExisteException;
import com.innovatech.logistica.messaging.event.PedidoPagadoEvent;
import com.innovatech.logistica.model.entity.Envio;
import com.innovatech.logistica.model.enums.EstadoEnvio;
import com.innovatech.logistica.repository.EnvioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EnvioServiceImpl implements EnvioService {

    private final EnvioRepository envioRepository;

    // ── POST /api/v1/logistica ─────────────────────────────────────
    @Override
    @Transactional
    public EnvioResponseDTO crearEnvio(EnvioRequestDTO request) {
        if (envioRepository.existsByPedidoId(request.getPedidoId())) {
            throw new EnvioYaExisteException(request.getPedidoId());
        }

        Envio envio = Envio.builder()
                .codigoSeguimiento(generarCodigoSeguimiento())
                .pedidoId(request.getPedidoId())
                .usuarioId(request.getUsuarioId())
                .estado(EstadoEnvio.PENDIENTE)
                .nombreDestinatario(request.getNombreDestinatario())
                .direccionDestino(request.getDireccionDestino())
                .ciudadDestino(request.getCiudadDestino())
                .regionDestino(request.getRegionDestino())
                .telefonoContacto(request.getTelefonoContacto())
                .observaciones(request.getObservaciones())
                .fechaEstimadaEntrega(request.getFechaEstimadaEntrega() != null
                        ? request.getFechaEstimadaEntrega()
                        : LocalDateTime.now().plusDays(5))
                .build();

        Envio guardado = envioRepository.save(envio);
        log.info("Envío creado manualmente — ID: {}, Código: {}", guardado.getId(), guardado.getCodigoSeguimiento());
        return toDTO(guardado);
    }

    // ── Creación automática desde evento RabbitMQ ──────────────────
    @Override
    @Transactional
    public EnvioResponseDTO crearEnvioDesdeEvento(PedidoPagadoEvent event) {
        if (envioRepository.existsByPedidoId(event.getPedidoId())) {
            log.warn("Evento duplicado ignorado — pedidoId: {}", event.getPedidoId());
            return obtenerPorPedidoId(event.getPedidoId());
        }

        Envio envio = Envio.builder()
                .codigoSeguimiento(generarCodigoSeguimiento())
                .pedidoId(event.getPedidoId())
                .usuarioId(event.getUsuarioId())
                .estado(EstadoEnvio.PENDIENTE)
                .nombreDestinatario(event.getNombreDestinatario())
                .direccionDestino(event.getDireccionDestino())
                .ciudadDestino(event.getCiudadDestino())
                .regionDestino(event.getRegionDestino())
                .telefonoContacto(event.getTelefonoContacto())
                .fechaEstimadaEntrega(LocalDateTime.now().plusDays(5))
                .build();

        Envio guardado = envioRepository.save(envio);
        log.info("Envío creado desde evento — PedidoID: {}, Código: {}", event.getPedidoId(), guardado.getCodigoSeguimiento());
        return toDTO(guardado);
    }

    // ── GET /api/v1/logistica ──────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<EnvioResponseDTO> listarEnvios() {
        return envioRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnvioResponseDTO> listarEnviosPorUsuario(Long usuarioId) {
        return envioRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ── GET /api/v1/logistica/{id} ─────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public EnvioResponseDTO obtenerPorId(Long id) {
        Envio envio = envioRepository.findById(id)
                .orElseThrow(() -> new EnvioNotFoundException(id));
        return toDTO(envio);
    }

    // ── GET /api/v1/logistica/pedido/{pedidoId} ───────────────────
    @Override
    @Transactional(readOnly = true)
    public EnvioResponseDTO obtenerPorPedidoId(Long pedidoId) {
        Envio envio = envioRepository.findByPedidoId(pedidoId)
                .orElseThrow(() -> new EnvioNotFoundException("No existe envío para pedidoId: " + pedidoId));
        return toDTO(envio);
    }

    // ── PUT /api/v1/logistica/{id} ────────────────────────────────
    @Override
    @Transactional
    public EnvioResponseDTO actualizarEnvio(Long id, EnvioRequestDTO request) {
        Envio envio = envioRepository.findById(id)
                .orElseThrow(() -> new EnvioNotFoundException(id));

        envio.setNombreDestinatario(request.getNombreDestinatario());
        envio.setDireccionDestino(request.getDireccionDestino());
        envio.setCiudadDestino(request.getCiudadDestino());
        envio.setRegionDestino(request.getRegionDestino());
        envio.setTelefonoContacto(request.getTelefonoContacto());
        envio.setObservaciones(request.getObservaciones());

        if (request.getFechaEstimadaEntrega() != null) {
            envio.setFechaEstimadaEntrega(request.getFechaEstimadaEntrega());
        }

        Envio actualizado = envioRepository.save(envio);
        log.info("Envío actualizado — ID: {}", id);
        return toDTO(actualizado);
    }

    // ── PATCH /api/v1/logistica/{id}/estado ───────────────────────
    @Override
    @Transactional
    public EnvioResponseDTO actualizarEstado(Long id, ActualizarEstadoDTO dto) {
        Envio envio = envioRepository.findById(id)
                .orElseThrow(() -> new EnvioNotFoundException(id));

        EstadoEnvio estadoAnterior = envio.getEstado();
        envio.setEstado(dto.getEstado());

        if (dto.getObservaciones() != null && !dto.getObservaciones().isBlank()) {
            envio.setObservaciones(dto.getObservaciones());
        }

        // Si se marca como entregado, se registra la fecha real
        if (dto.getEstado() == EstadoEnvio.ENTREGADO) {
            envio.setFechaEntregaReal(LocalDateTime.now());
        }

        Envio actualizado = envioRepository.save(envio);
        log.info("Estado actualizado — ID: {} | {} → {}", id, estadoAnterior, dto.getEstado());
        return toDTO(actualizado);
    }

    // ── DELETE /api/v1/logistica/{id} ─────────────────────────────
    @Override
    @Transactional
    public void eliminarEnvio(Long id) {
        if (!envioRepository.existsById(id)) {
            throw new EnvioNotFoundException(id);
        }
        envioRepository.deleteById(id);
        log.info("Envío eliminado — ID: {}", id);
    }

    // ── Generador de código de seguimiento ────────────────────────
    // Formato: IT-XXXXXXXX (IT = InnovaTech + 8 chars hex en mayúsculas)
    private String generarCodigoSeguimiento() {
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return "IT-" + uuid;
    }

    // ── Mapper entidad → DTO ──────────────────────────────────────
    private EnvioResponseDTO toDTO(Envio envio) {
        return EnvioResponseDTO.builder()
                .id(envio.getId())
                .codigoSeguimiento(envio.getCodigoSeguimiento())
                .pedidoId(envio.getPedidoId())
                .usuarioId(envio.getUsuarioId())
                .estado(envio.getEstado())
                .nombreDestinatario(envio.getNombreDestinatario())
                .direccionDestino(envio.getDireccionDestino())
                .ciudadDestino(envio.getCiudadDestino())
                .regionDestino(envio.getRegionDestino())
                .telefonoContacto(envio.getTelefonoContacto())
                .observaciones(envio.getObservaciones())
                .fechaEstimadaEntrega(envio.getFechaEstimadaEntrega())
                .fechaEntregaReal(envio.getFechaEntregaReal())
                .fechaCreacion(envio.getFechaCreacion())
                .fechaActualizacion(envio.getFechaActualizacion())
                .build();
    }
}
