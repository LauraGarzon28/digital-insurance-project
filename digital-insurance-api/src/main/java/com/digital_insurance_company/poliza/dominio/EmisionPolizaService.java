package com.digital_insurance_company.poliza.dominio;

import java.time.LocalDate;
import java.util.Objects;

public class EmisionPolizaService {

    private final PolizaFactory factory;

    public EmisionPolizaService(PolizaFactory factory) {
        this.factory = Objects.requireNonNull(factory);
    }

    public Poliza emitir(DatosCotizacionParaEmision cotizacion, LocalDate fechaInicio, LocalDate fechaFin) {
        Objects.requireNonNull(cotizacion, "La cotización es obligatoria");
        if (!cotizacion.aceptada()) {
            throw new EmisionNoPermitidaException(
                    "No se emite póliza: la cotización %s no está aceptada".formatted(cotizacion.cotizacionId()));
        }
        return factory.nueva(cotizacion.clienteId(), cotizacion.cotizacionId(),
                cotizacion.valorAsegurado(), fechaInicio, fechaFin);
    }
}
