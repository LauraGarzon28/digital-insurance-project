package com.auditoria_service;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * El consumidor declara su propia cola y su binding para que la infraestructura
 * funcione aunque el productor aún no haya arrancado.
 */
@Configuration
public class RabbitConfiguration {

    @Bean
    TopicExchange exchangeSiniestros(@Value("${eventos.exchange}") String nombre) {
        return new TopicExchange(nombre, true, false);
    }

    @Bean
    Queue colaAuditoria(@Value("${eventos.cola-auditoria}") String nombre) {
        return QueueBuilder.durable(nombre).build();
    }

    @Bean
    Binding bindingAuditoria(Queue colaAuditoria, TopicExchange exchangeSiniestros,
            @Value("${eventos.routing-key}") String routingKey) {
        return BindingBuilder.bind(colaAuditoria).to(exchangeSiniestros).with(routingKey);
    }

    @Bean
    MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
