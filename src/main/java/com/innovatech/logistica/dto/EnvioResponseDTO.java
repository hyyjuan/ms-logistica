package com.innovatech.logistica.dto;

import com.innovatech.logistica.model.enums.EstadoEnvio;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnvioResponseDTO {

    private Long id;
    private String codigoSeguimiento;
    private Long pedidoId;
    private Long usuarioId;
    private EstadoEnvio estado;
    private String nombreDestinatario;
    private String direccionDestino;
    private String ciudadDestino;
    private String regionDestino;
    private String telefonoContacto;
    private String observaciones;
    private LocalDateTime fechaEstimadaEntrega;
    private LocalDateTime fechaEntregaReal;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
