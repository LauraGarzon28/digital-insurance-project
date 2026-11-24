package com.digital_insurance_company.polizas_service.infraestructura.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfiguration {

     /** Routing key con la que Siniestros responde la Saga de cancelación. */
    public static final String RK_CANCELACION_VERIFICADA = "siniestros.cancelacion-verificada";

    /** Cola propia de Pólizas para esa respuesta. */
    public static final String COLA_CANCELACION_VERIFICADA = "polizas.siniestros-cancelacion.queue";

    @Bean
    public TopicExchange exchangeDeEventos(@Value("${polizas.mensajeria.exchange}") String nombre) {
        return new TopicExchange(nombre, true, false);   // durable, sin auto-borrado
    }

    @Bean
    public Queue colaCancelacionVerificada() {
        return new Queue(COLA_CANCELACION_VERIFICADA, true);   // durable: sobrevive a un reinicio del broker
    }

    @Bean
    public Binding bindingCancelacionVerificada(Queue colaCancelacionVerificada, TopicExchange exchangeDeEventos) {
        return BindingBuilder.bind(colaCancelacionVerificada)
                .to(exchangeDeEventos)
                .with(RK_CANCELACION_VERIFICADA);
    }
}
