package com.digital_insurance_company.polizas_service.infraestructura.salida.mensajeria;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OutboxPoller {

    private static final Logger log = LoggerFactory.getLogger(OutboxPoller.class);

    private final OutboxEventRepository repositorio;
    private final RabbitTemplate rabbitTemplate;
    private final String exchange;

    public OutboxPoller(OutboxEventRepository repositorio,
                        RabbitTemplate rabbitTemplate,
                        @Value("${polizas.mensajeria.exchange}") String exchange) {
        this.repositorio = repositorio;
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
    }

    @Scheduled(fixedDelay = 2000)
    @Transactional
    public void publicarPendientes() {
        List<OutboxEvent> pendientes = repositorio.findTop50ByPublicadoFalseOrderByIdAsc();
        for (OutboxEvent evento : pendientes) {
            try {
                rabbitTemplate.convertAndSend(exchange, evento.getTopico(), evento.getPayload());
                evento.marcarPublicado();
                log.info("Evento {} publicado en exchange {} con routing key {}", evento.getId(), exchange, evento.getTopico());
            } catch (AmqpException e) {
                log.warn("No se pudo publicar el evento {} (se reintentará): {}", evento.getId(), e.getMessage());
                return; // conserva el orden: no seguimos con los siguientes
            }
        }
    }
}