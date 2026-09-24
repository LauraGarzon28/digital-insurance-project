package com.digital_insurance_company.poliza;

import java.time.LocalDate;
import java.util.Objects;

import com.digital_insurance_company.cotizacion.Cotizacion;
import com.digital_insurance_company.cotizacion.EstadoCotizacion;

public class EmisionPolizaService {

    private final PolizaFactory factory;

    public EmisionPolizaService(PolizaFactory factory) {
        this.factory = Objects.requireNonNull(factory);
    }

    public Poliza emitir(Cotizacion cotizacion, LocalDate fechaInicio, LocalDate fechaFin) {
        Objects.requireNonNull(cotizacion, "La cotización es obligatoria");
        if (cotizacion.estado() != EstadoCotizacion.ACEPTADA) {
            throw new EmisionNoPermitidaException(
                    "No se emite póliza: la cotización está %s y se esperaba ACEPTADA"
                            .formatted(cotizacion.estado()));
        }
        return factory.nueva(cotizacion.clienteId(), cotizacion.id(), cotizacion.riesgo().valorAsegurado(), fechaInicio, fechaFin);
    }
}
