package com.digital_insurance_company.cotizacion.aplicacion;

import java.util.Objects;
import java.util.UUID;

import com.digital_insurance_company.cotizacion.dominio.Cotizacion;
import com.digital_insurance_company.cotizacion.dominio.CotizacionFactory;
import com.digital_insurance_company.cotizacion.dominio.EvaluacionRiesgoService;
import com.digital_insurance_company.cotizacion.dominio.ReglasSuscripcion;
import com.digital_insurance_company.cotizacion.dominio.Riesgo;

public class CotizacionApplicationService implements CotizacionUseCase {

    private final CotizacionFactory cotizacionFactory;
    private final EvaluacionRiesgoService evaluacionRiesgoService;

    public CotizacionApplicationService(CotizacionFactory cotizacionFactory,
            EvaluacionRiesgoService evaluacionRiesgoService) {
        this.cotizacionFactory = Objects.requireNonNull(cotizacionFactory);
        this.evaluacionRiesgoService = Objects.requireNonNull(evaluacionRiesgoService);
    }

    @Override
    public Cotizacion solicitar(UUID clienteId, Riesgo riesgo) {
        return cotizacionFactory.crear(clienteId, riesgo);
    }

    @Override
    public void evaluar(Cotizacion cotizacion, ReglasSuscripcion reglas) {
        Objects.requireNonNull(cotizacion, "La cotización es obligatoria");
        cotizacion.registrarEvaluacion(
                evaluacionRiesgoService.evaluar(cotizacion.riesgo(), reglas));
    }

    @Override
    public void aprobarRevisionManual(Cotizacion cotizacion) {
        Objects.requireNonNull(cotizacion, "La cotización es obligatoria")
                .aprobarRevisionManual();
    }

    @Override
    public void rechazar(Cotizacion cotizacion) {
        Objects.requireNonNull(cotizacion, "La cotización es obligatoria")
                .rechazar();
    }

    @Override
    public void aceptar(Cotizacion cotizacion) {
        Objects.requireNonNull(cotizacion, "La cotización es obligatoria")
                .aceptar();
    }
}
