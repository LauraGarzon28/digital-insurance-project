package com.auditoria_service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Hecho ocurrido en el contexto de Siniestros. El payload viaja por RabbitMQ y
 * conserva el {@code mensajeId} para poder descartar duplicados.
 */
public record EventoSiniestro(
        UUID mensajeId,
        String tipo,
        UUID siniestroId,
        UUID polizaId,
        String estado,
        BigDecimal montoAprobado,
        Instant ocurridoEn) {
}
