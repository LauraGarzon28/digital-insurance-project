package com.digital_insurance_company.cotizacion;

import java.util.UUID;

public class Cotizacion {
    
    private final UUID id;
    private final UUID clienteId;  
    private final Riesgo riesgo;
    private Prima prima;            
    private EstadoCotizacion estado;

    public Cotizacion(UUID id, UUID clienteId, Riesgo riesgo) {
        if (id == null || clienteId == null || riesgo == null) {
            throw new IllegalArgumentException("Cotización requiere id, clienteId y riesgo");
        }
        this.id = id;
        this.clienteId = clienteId;
        this.riesgo = riesgo;
        this.estado = EstadoCotizacion.SOLICITADA;
    }

    public void registrarEvaluacion(EvaluacionRiesgo evaluacion) {
        exigirEstado(EstadoCotizacion.SOLICITADA);
        this.prima = evaluacion.prima();
        this.estado = evaluacion.requiereRevisionManual()
                ? EstadoCotizacion.EN_REVISION_MANUAL
                : EstadoCotizacion.COTIZADA;
    }

    public void aprobarRevisionManual() {
        exigirEstado(EstadoCotizacion.EN_REVISION_MANUAL);
        this.estado = EstadoCotizacion.COTIZADA;
    }

    public void rechazar() {
        exigirEstado(EstadoCotizacion.EN_REVISION_MANUAL);
        this.estado = EstadoCotizacion.RECHAZADA;
    }

    public void aceptar() {
        exigirEstado(EstadoCotizacion.COTIZADA);
        this.estado = EstadoCotizacion.ACEPTADA;
    }

    private void exigirEstado(EstadoCotizacion esperado) {
        if (estado != esperado) {
            throw new IllegalStateException(
                    "Operación inválida: la cotización está " + estado + " y se esperaba " + esperado);
        }
    }

    public UUID id() { return id; }
    public UUID clienteId() { return clienteId; }
    public Riesgo riesgo() { return riesgo; }
    public Prima prima() { return prima; }
    public EstadoCotizacion estado() { return estado; }
}
