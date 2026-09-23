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
    
    public Poliza emitirPoliza(Cotizacion cotizacion, LocalDate fechaEmision) {
        Objects.requireNonNull(cotizacion, "Debe existir una cotización");
        if (cotizacion.estado() != EstadoCotizacion.ACEPTADA) {
            throw new EmisionNoPermitidaException("La cotización está en estado %s y debe ser ACEPTADA para poder emitir la póliza"
            .formatted(cotizacion.estado()));
        }
        VigenciaPoliza vigencia = new VigenciaPoliza(fechaEmision, fechaEmision.plusYears(1));
        return null;
    }
    
}
