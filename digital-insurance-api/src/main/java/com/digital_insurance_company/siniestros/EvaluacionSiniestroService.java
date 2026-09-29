package com.digital_insurance_company.siniestros;

import java.math.BigDecimal;

public class EvaluacionSiniestroService {

    public void evaluar(Siniestro siniestro, EstadoPolizaReferencia estadoPoliza, boolean esFraude, BigDecimal montoPropuesto) {
        if (siniestro == null) {
            throw new IllegalArgumentException("El siniestro no puede ser nulo");
        }

        siniestro.marcarEnEvaluacion();

        // Regla de negocio: rechaza si la póliza está vencida o cancelada
        if (estadoPoliza == EstadoPolizaReferencia.CANCELADA || estadoPoliza == EstadoPolizaReferencia.VENCIDA) {
            siniestro.rechazar();
            return;
        }

        if (esFraude) {
            siniestro.rechazar();
            return;
        }

        // Si pasa las reglas, se aprueba
        siniestro.aprobar(montoPropuesto);
    }
}
