package com.digital_insurance_company.cotizacion.aplicacion;

import java.util.UUID;

import com.digital_insurance_company.cotizacion.dominio.Cotizacion;
import com.digital_insurance_company.cotizacion.dominio.ReglasSuscripcion;
import com.digital_insurance_company.cotizacion.dominio.Riesgo;

public interface CotizacionUseCase {

    Cotizacion solicitar(UUID clienteId, Riesgo riesgo);

    void evaluar(Cotizacion cotizacion, ReglasSuscripcion reglas);

    void aprobarRevisionManual(Cotizacion cotizacion);

    void rechazar(Cotizacion cotizacion);

    void aceptar(Cotizacion cotizacion);
}
