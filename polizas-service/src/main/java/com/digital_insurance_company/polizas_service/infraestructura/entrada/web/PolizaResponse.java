package com.digital_insurance_company.polizas_service.infraestructura.entrada.web;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.digital_insurance_company.polizas_service.dominio.EstadoPoliza;
import com.digital_insurance_company.polizas_service.dominio.Poliza;

public record PolizaResponse(
        UUID id,
        UUID clienteId,
        BigDecimal valorAsegurado,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        EstadoPoliza estado) {

    public static PolizaResponse desde(Poliza poliza) {
        LocalDate hoy = LocalDate.now();
        return new PolizaResponse(
                poliza.id(),
                poliza.clienteId(),
                poliza.valorAsegurado().monto(),
                poliza.vigencia().fechaInicio(),
                poliza.vigencia().fechaFin(),
                poliza.estadoEn(hoy));
    }
}
