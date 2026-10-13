package com.digital_insurance_company.cotizacion.aplicacion;

import java.util.UUID;
import java.util.Optional;

import com.digital_insurance_company.cotizacion.dominio.Cotizacion;
import com.digital_insurance_company.cotizacion.dominio.ReglasSuscripcion;
import com.digital_insurance_company.cotizacion.dominio.Riesgo;

public interface CotizacionUseCase {

    Cotizacion solicitar(UUID clienteId, Riesgo riesgo);

    Optional<Cotizacion> buscarPorId(UUID id);

    Cotizacion evaluar(UUID id, ReglasSuscripcion reglas);

    Cotizacion aprobarRevisionManual(UUID id);

    Cotizacion rechazar(UUID id);

    Cotizacion aceptar(UUID id);
}
