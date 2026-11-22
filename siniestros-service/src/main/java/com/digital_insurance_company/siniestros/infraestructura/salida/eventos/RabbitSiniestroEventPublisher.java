package com.digital_insurance_company.siniestros.infraestructura.salida.eventos;

import java.time.Instant;
import java.util.UUID;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.digital_insurance_company.siniestros.dominio.Siniestro;
import com.digital_insurance_company.siniestros.dominio.puertos.PublicadorEventosSiniestro;

@Component
public class RabbitSiniestroEventPublisher implements PublicadorEventosSiniestro {

    private final RabbitTemplate rabbitTemplate;
    private final String exchange;
    private final String routingKey;

    public RabbitSiniestroEventPublisher(RabbitTemplate rabbitTemplate,
            @Value("${eventos.exchange}") String exchange,
            @Value("${eventos.routing-key}") String routingKey) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
        this.routingKey = routingKey;
    }

    @Override
    public void siniestroReportado(Siniestro siniestro) {
        publicar(new EventoSiniestro(
                UUID.randomUUID(),
                "SiniestroReportado",
                siniestro.getId(),
                siniestro.getPolizaId(),
                siniestro.getEstado().name(),
                null,
                Instant.now()));
    }

    @Override
    public void siniestroResuelto(Siniestro siniestro) {
        publicar(new EventoSiniestro(
                UUID.randomUUID(),
                "SiniestroResuelto",
                siniestro.getId(),
                siniestro.getPolizaId(),
                siniestro.getEstado().name(),
                siniestro.getMontoAprobado() == null ? null : siniestro.getMontoAprobado().valor(),
                Instant.now()));
    }

    private void publicar(EventoSiniestro evento) {
        rabbitTemplate.convertAndSend(exchange, routingKey, evento);
    }

    public record EventoSiniestro(
            UUID mensajeId,
            String tipo,
            UUID siniestroId,
            UUID polizaId,
            String estado,
            java.math.BigDecimal montoAprobado,
            Instant ocurridoEn) {
    }
}
