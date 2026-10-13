package com.digital_insurance_company.poliza.dominio.puertos;

import java.time.LocalDate;
import java.util.UUID;

import com.digital_insurance_company.poliza.dominio.Poliza;

public interface PolizaUseCase {

    Poliza emitirPoliza(UUID cotizacionId, LocalDate fechaInicio, LocalDate fechaFin);

    Poliza renovarPoliza(UUID polizaId);

    Poliza cancelarPoliza(UUID polizaId);

    Poliza consultarPoliza(UUID polizaId);
}
