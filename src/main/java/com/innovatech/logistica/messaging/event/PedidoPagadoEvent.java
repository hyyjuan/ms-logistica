package com.innovatech.logistica.messaging.event;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Evento publicado por ms-pedidos cuando un pago es confirmado.
 * ms-logistica lo consume para generar automáticamente el registro de envío.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class PedidoPagadoEvent implements Serializable {

    private Long pedidoId;
    private Long usuarioId;

    // Datos de destino que ms-pedidos conoce al momento del pago
    private String nombreDestinatario;
    private String direccionDestino;
    private String ciudadDestino;
    private String regionDestino;
    private String telefonoContacto;

    private LocalDateTime fechaPago;
}
