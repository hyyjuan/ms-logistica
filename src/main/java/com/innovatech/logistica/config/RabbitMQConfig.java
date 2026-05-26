package com.innovatech.logistica.config;

import java.util.Map;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
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

    @Value("${logistica.rabbitmq.dead-letter.exchange}")
    private String deadLetterExchange;

    @Value("${logistica.rabbitmq.dead-letter.queue}")
    private String deadLetterQueue;

    @Value("${logistica.rabbitmq.dead-letter.routing-key}")
    private String deadLetterRoutingKey;

    @Bean
    public Queue pedidoPagadoQueue() {
        return QueueBuilder.durable(queue)
                .withArguments(Map.of(
                        "x-dead-letter-exchange", deadLetterExchange,
                        "x-dead-letter-routing-key", deadLetterRoutingKey))
                .build();
    }

    @Bean
    public TopicExchange pedidoExchange() {
        return new TopicExchange(exchange);
    }

    @Bean
    public Binding binding(Queue pedidoPagadoQueue, TopicExchange pedidoExchange) {
        return BindingBuilder
                .bind(pedidoPagadoQueue)
                .to(pedidoExchange)
                .with(routingKey);
    }

    @Bean
    public Queue pedidoPagadoDeadLetterQueue() {
        return QueueBuilder.durable(deadLetterQueue).build();
    }

    @Bean
    public TopicExchange pedidoDeadLetterExchange() {
        return new TopicExchange(deadLetterExchange);
    }

    @Bean
    public Binding deadLetterBinding(Queue pedidoPagadoDeadLetterQueue, TopicExchange pedidoDeadLetterExchange) {
        return BindingBuilder
                .bind(pedidoPagadoDeadLetterQueue)
                .to(pedidoDeadLetterExchange)
                .with(deadLetterRoutingKey);
    }

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
