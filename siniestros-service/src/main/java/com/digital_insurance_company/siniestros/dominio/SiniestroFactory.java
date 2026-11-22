package com.digital_insurance_company.siniestros.dominio;

import java.math.BigDecimal;
import java.util.UUID;

public class SiniestroFactory {

    /**
     * Reconstituye el agregado a partir del estado persistido. No valida reglas de
     * creación: el objeto ya existía y su invariante vive en el agregado.
     */
    public Siniestro reconstituir(UUID id, UUID polizaId, String descripcion,
            EstadoSiniestro estado, BigDecimal valorAseguradoPoliza, BigDecimal montoAprobado) {
        Siniestro siniestro = new Siniestro(id, polizaId, descripcion, valorAseguradoPoliza);
        siniestro.restaurar(estado, montoAprobado);
        return siniestro;
    }

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
