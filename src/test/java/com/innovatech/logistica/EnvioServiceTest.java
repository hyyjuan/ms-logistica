package com.innovatech.logistica;

import com.innovatech.logistica.dto.ActualizarEstadoDTO;
import com.innovatech.logistica.dto.EnvioRequestDTO;
import com.innovatech.logistica.dto.EnvioResponseDTO;
import com.innovatech.logistica.exception.EnvioNotFoundException;
import com.innovatech.logistica.exception.EnvioYaExisteException;
import com.innovatech.logistica.messaging.event.PedidoPagadoEvent;
import com.innovatech.logistica.model.entity.Envio;
import com.innovatech.logistica.model.enums.EstadoEnvio;
import com.innovatech.logistica.repository.EnvioRepository;
import com.innovatech.logistica.service.EnvioServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnvioServiceTest {

    @Mock
    private EnvioRepository envioRepository;

    @InjectMocks
    private EnvioServiceImpl envioService;

    private Envio envioMock;
    private EnvioRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        envioMock = Envio.builder()
                .id(1L)
                .codigoSeguimiento("IT-ABCD1234")
                .pedidoId(10L)
                .usuarioId(5L)
                .estado(EstadoEnvio.PENDIENTE)
                .nombreDestinatario("Juan Vargas")
                .direccionDestino("Av. Siempre Viva 742")
                .ciudadDestino("Santiago")
                .regionDestino("Metropolitana")
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();

        requestDTO = EnvioRequestDTO.builder()
                .pedidoId(10L)
                .usuarioId(5L)
                .nombreDestinatario("Juan Vargas")
                .direccionDestino("Av. Siempre Viva 742")
                .ciudadDestino("Santiago")
                .regionDestino("Metropolitana")
                .build();
    }

    @Test
    @DisplayName("Debe crear un envío correctamente")
    void crearEnvio_exitoso() {
        when(envioRepository.existsByPedidoId(10L)).thenReturn(false);
        when(envioRepository.save(any(Envio.class))).thenReturn(envioMock);

        EnvioResponseDTO resultado = envioService.crearEnvio(requestDTO);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getCodigoSeguimiento()).isEqualTo("IT-ABCD1234");
        assertThat(resultado.getEstado()).isEqualTo(EstadoEnvio.PENDIENTE);
        verify(envioRepository, times(1)).save(any(Envio.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si el pedido ya tiene envío")
    void crearEnvio_duplicado() {
        when(envioRepository.existsByPedidoId(10L)).thenReturn(true);

        assertThatThrownBy(() -> envioService.crearEnvio(requestDTO))
                .isInstanceOf(EnvioYaExisteException.class);

        verify(envioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe crear envío automáticamente desde evento RabbitMQ")
    void crearEnvioDesdeEvento_exitoso() {
        PedidoPagadoEvent event = PedidoPagadoEvent.builder()
                .pedidoId(10L)
                .usuarioId(5L)
                .nombreDestinatario("Juan Vargas")
                .direccionDestino("Av. Siempre Viva 742")
                .ciudadDestino("Santiago")
                .fechaPago(LocalDateTime.now())
                .build();

        when(envioRepository.existsByPedidoId(10L)).thenReturn(false);
        when(envioRepository.save(any(Envio.class))).thenReturn(envioMock);

        EnvioResponseDTO resultado = envioService.crearEnvioDesdeEvento(event);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getPedidoId()).isEqualTo(10L);
    }

    @Test
    @DisplayName("Debe retornar todos los envíos")
    void listarEnvios() {
        when(envioRepository.findAll()).thenReturn(List.of(envioMock));

        List<EnvioResponseDTO> resultado = envioService.listarEnvios();

        assertThat(resultado).hasSize(1);
    }

    @Test
    @DisplayName("Debe obtener envío por ID")
    void obtenerPorId_exitoso() {
        when(envioRepository.findById(1L)).thenReturn(Optional.of(envioMock));

        EnvioResponseDTO resultado = envioService.obtenerPorId(1L);

        assertThat(resultado.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción si no existe el ID")
    void obtenerPorId_noExiste() {
        when(envioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> envioService.obtenerPorId(99L))
                .isInstanceOf(EnvioNotFoundException.class);
    }

    @Test
    @DisplayName("Debe actualizar el estado y registrar fecha de entrega cuando es ENTREGADO")
    void actualizarEstado_entregado() {
        when(envioRepository.findById(1L)).thenReturn(Optional.of(envioMock));
        when(envioRepository.save(any(Envio.class))).thenAnswer(inv -> inv.getArgument(0));

        ActualizarEstadoDTO dto = new ActualizarEstadoDTO(EstadoEnvio.ENTREGADO, "Entregado en portería");
        EnvioResponseDTO resultado = envioService.actualizarEstado(1L, dto);

        assertThat(resultado.getEstado()).isEqualTo(EstadoEnvio.ENTREGADO);
        assertThat(resultado.getFechaEntregaReal()).isNotNull();
    }

    @Test
    @DisplayName("Debe eliminar un envío existente")
    void eliminarEnvio_exitoso() {
        when(envioRepository.existsById(1L)).thenReturn(true);

        assertThatCode(() -> envioService.eliminarEnvio(1L)).doesNotThrowAnyException();
        verify(envioRepository).deleteById(1L);
    }
}
