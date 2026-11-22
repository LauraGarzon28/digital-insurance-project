package com.auditoria_service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Adaptador primario de mensajería: consume los hechos publicados por
 * {@code siniestros-service}. Delega en {@link AuditoriaService}, que aplica la
 * regla de idempotencia.
 */
@Component
public class SiniestroEventoListener {

    private final AuditoriaService auditoriaService;

    public SiniestroEventoListener(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    @RabbitListener(queues = "${eventos.cola-auditoria}")
    public void recibir(EventoSiniestro evento) {
        auditoriaService.registrar(evento);
    }
}
