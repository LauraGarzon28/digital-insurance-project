package com.auditoria_service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record DecisionResponse(
        UUID mensajeId,
        String tipo,
        UUID siniestroId,
        UUID polizaId,
        String estado,
        BigDecimal montoAprobado,
        Instant ocurridoEn,
        Instant registradoEn) {

    static DecisionResponse desde(DecisionSiniestro decision) {
        return new DecisionResponse(
                decision.getMensajeId(),
                decision.getTipo(),
                decision.getSiniestroId(),
                decision.getPolizaId(),
                decision.getEstado(),
                decision.getMontoAprobado(),
                decision.getOcurridoEn(),
                decision.getRegistradoEn());
    }
}
