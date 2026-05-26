package com.innovatech.logistica.messaging.consumer;

import com.innovatech.logistica.messaging.event.PedidoPagadoEvent;
import com.innovatech.logistica.service.EnvioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PedidoPagadoConsumer {

    private final EnvioService envioService;

    @RabbitListener(queues = "${logistica.rabbitmq.queue}")
    public void procesarPedidoPagado(PedidoPagadoEvent event) {
        log.info("Evento recibido PedidoPagado | pedidoId: {} | usuarioId: {}",
                event.getPedidoId(), event.getUsuarioId());

        try {
            var envio = envioService.crearEnvioDesdeEvento(event);
            log.info("Envio generado desde evento | codigoSeguimiento: {} | pedidoId: {}",
                    envio.getCodigoSeguimiento(), event.getPedidoId());
        } catch (Exception ex) {
            // El contenedor rechaza el mensaje y RabbitMQ lo redirige a la DLQ configurada.
            log.error("Error procesando evento PedidoPagado para pedidoId: {} | {}",
                    event.getPedidoId(), ex.getMessage());
            throw ex;
        }
    }
}
