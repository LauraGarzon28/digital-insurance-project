package com.digital_insurance_company.cotizacion.infraestructura.entrada.web;

import java.math.BigDecimal;
import java.util.UUID;

import com.digital_insurance_company.cotizacion.dominio.Cotizacion;

public record CotizacionResponse(
        UUID id,
        UUID clienteId,
        String descripcionRiesgo,
        BigDecimal valorAsegurado,
        int puntajeRiesgo,
        String estado,
        PrimaResponse prima) {

    public static CotizacionResponse desde(Cotizacion cotizacion) {
        PrimaResponse prima = cotizacion.prima() == null
                ? null
                : new PrimaResponse(
                        cotizacion.prima().monto(),
                        cotizacion.prima().versionReglas());

        return new CotizacionResponse(
                cotizacion.id(),
                cotizacion.clienteId(),
                cotizacion.riesgo().descripcion(),
                cotizacion.riesgo().valorAsegurado(),
                cotizacion.riesgo().puntaje(),
                cotizacion.estado().name(),
                prima);
    }

    public record PrimaResponse(BigDecimal monto, String versionReglas) {
    }
}
