package com.innovatech.logistica.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnvioRequestDTO {

    @NotNull(message = "El pedidoId es obligatorio")
    @Positive(message = "El pedidoId debe ser mayor a cero")
    private Long pedidoId;

    @NotNull(message = "El usuarioId es obligatorio")
    @Positive(message = "El usuarioId debe ser mayor a cero")
    private Long usuarioId;

    @NotBlank(message = "El nombre del destinatario es obligatorio")
    @Size(max = 150, message = "El nombre del destinatario no puede exceder 150 caracteres")
    private String nombreDestinatario;

    @NotBlank(message = "La direccion de destino es obligatoria")
    @Size(max = 255, message = "La direccion de destino no puede exceder 255 caracteres")
    private String direccionDestino;

    @NotBlank(message = "La ciudad de destino es obligatoria")
    @Size(max = 120, message = "La ciudad de destino no puede exceder 120 caracteres")
    private String ciudadDestino;

    @Size(max = 120, message = "La region de destino no puede exceder 120 caracteres")
    private String regionDestino;

    @Size(max = 30, message = "El telefono de contacto no puede exceder 30 caracteres")
    private String telefonoContacto;

    @Size(max = 500, message = "Las observaciones no pueden exceder 500 caracteres")
    private String observaciones;

    private LocalDateTime fechaEstimadaEntrega;
}
