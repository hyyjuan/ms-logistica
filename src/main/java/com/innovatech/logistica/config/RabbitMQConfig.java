package com.innovatech.logistica.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${logistica.rabbitmq.queue}")
    private String queue;

    @Value("${logistica.rabbitmq.exchange}")
    private String exchange;

    @Value("${logistica.rabbitmq.routing-key}")
    private String routingKey;

    // ── Cola durable: sobrevive reinicios del broker ───────────────
    @Bean
    public Queue pedidoPagadoQueue() {
        return QueueBuilder.durable(queue).build();
    }

    // ── Topic Exchange: permite múltiples consumidores del evento ──
    @Bean
    public TopicExchange pedidoExchange() {
        return new TopicExchange(exchange);
    }

    // ── Binding: conecta la cola con el exchange por routing key ──
    @Bean
    public Binding binding(Queue pedidoPagadoQueue, TopicExchange pedidoExchange) {
        return BindingBuilder
                .bind(pedidoPagadoQueue)
                .to(pedidoExchange)
                .with(routingKey);
    }

    // ── Conversor JSON para serializar/deserializar mensajes ───────
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}
