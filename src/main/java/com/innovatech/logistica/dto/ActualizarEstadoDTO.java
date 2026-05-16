package com.innovatech.logistica.dto;

import com.innovatech.logistica.model.enums.EstadoEnvio;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActualizarEstadoDTO {

    @NotNull(message = "El estado es obligatorio")
    private EstadoEnvio estado;

    private String observaciones;
}
