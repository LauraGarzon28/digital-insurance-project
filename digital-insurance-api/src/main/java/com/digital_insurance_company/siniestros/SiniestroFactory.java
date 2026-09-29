package com.digital_insurance_company.siniestros;

import java.math.BigDecimal;
import java.util.UUID;

public class SiniestroFactory {

    public Siniestro reportarSiniestro(UUID polizaId, String descripcion, BigDecimal valorAseguradoPoliza) {
        if (polizaId == null) {
            throw new IllegalArgumentException("El ID de la póliza es requerido para reportar un siniestro");
        }
        if (descripcion == null || descripcion.isBlank()) {
            throw new IllegalArgumentException("La descripción del siniestro no puede estar vacía");
        }
        if (valorAseguradoPoliza == null || valorAseguradoPoliza.signum() <= 0) {
            throw new IllegalArgumentException("El valor asegurado de la póliza debe ser mayor a cero");
        }

        return new Siniestro(UUID.randomUUID(), polizaId, descripcion, valorAseguradoPoliza);
    }
}
