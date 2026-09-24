package com.digital_insurance_company.poliza;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class PolizaFactory {

    public Poliza nueva(UUID clienteId, UUID cotizacionId, BigDecimal valorAsegurado,
            LocalDate fechaInicio, LocalDate fechaFin) {
        Objects.requireNonNull(clienteId, "clienteId es obligatorio");
        Objects.requireNonNull(cotizacionId, "cotizacionId es obligatorio");
        return new Poliza(
                UUID.randomUUID(),
                clienteId,
                cotizacionId,
                new ValorAsegurado(valorAsegurado),
                new VigenciaPoliza(fechaInicio, fechaFin),
                false);
    }

    public Poliza reconstruir(UUID id, UUID clienteId, UUID cotizacionId, BigDecimal valorAsegurado,
            LocalDate fechaInicio, LocalDate fechaFin, boolean cancelada) {
        Objects.requireNonNull(id, "id es obligatorio");
        return new Poliza(
                id,
                Objects.requireNonNull(clienteId, "clienteId es obligatorio"),
                Objects.requireNonNull(cotizacionId, "cotizacionId es obligatorio"),
                new ValorAsegurado(valorAsegurado),
                new VigenciaPoliza(fechaInicio, fechaFin),
                cancelada);
    }
}
