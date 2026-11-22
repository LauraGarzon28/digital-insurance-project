package com.digital_insurance_company.siniestros.infraestructura.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Declara el exchange, la cola de auditoría y su binding. La cola es durable y
 * sobrevive a la caída del consumidor: los eventos se acumulan hasta que vuelva.
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
}
