package com.cotizaciones_service.cotizacion.dominio;

import java.util.UUID;
import java.util.Objects;

public class Cotizacion {

    private final UUID id;
    private final UUID clienteId;
    private final Riesgo riesgo;
    private Prima prima;
    private EstadoCotizacion estado;

    Cotizacion(UUID id, UUID clienteId, Riesgo riesgo) {
        if (id == null || clienteId == null || riesgo == null) {
            throw new IllegalArgumentException("Cotización requiere id, clienteId y riesgo");
        }
        this.id = id;
        this.clienteId = clienteId;
        this.riesgo = riesgo;
        this.estado = EstadoCotizacion.SOLICITADA;
    }

    public static Cotizacion reconstituir(UUID id, UUID clienteId, Riesgo riesgo,
            Prima prima, EstadoCotizacion estado) {
        Objects.requireNonNull(estado, "El estado es obligatorio");
        Cotizacion cotizacion = new Cotizacion(id, clienteId, riesgo);
        cotizacion.prima = prima;
        cotizacion.estado = estado;
        return cotizacion;
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

    public UUID id() {
        return id;
    }

    public UUID clienteId() {
        return clienteId;
    }

    public Riesgo riesgo() {
        return riesgo;
    }

    public Prima prima() {
        return prima;
    }

    public EstadoCotizacion estado() {
        return estado;
    }
}
