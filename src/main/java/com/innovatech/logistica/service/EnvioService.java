package com.innovatech.logistica.service;

import com.innovatech.logistica.dto.ActualizarEstadoDTO;
import com.innovatech.logistica.dto.EnvioRequestDTO;
import com.innovatech.logistica.dto.EnvioResponseDTO;
import com.innovatech.logistica.messaging.event.PedidoPagadoEvent;

import java.util.List;

public interface EnvioService {

    EnvioResponseDTO crearEnvio(EnvioRequestDTO request);

    EnvioResponseDTO crearEnvioDesdeEvento(PedidoPagadoEvent event);

    List<EnvioResponseDTO> listarEnvios();

    List<EnvioResponseDTO> listarEnviosPorUsuario(Long usuarioId);

    EnvioResponseDTO obtenerPorId(Long id);

    EnvioResponseDTO obtenerPorPedidoId(Long pedidoId);

    EnvioResponseDTO actualizarEnvio(Long id, EnvioRequestDTO request);

    EnvioResponseDTO actualizarEstado(Long id, ActualizarEstadoDTO dto);

    void eliminarEnvio(Long id);
}
