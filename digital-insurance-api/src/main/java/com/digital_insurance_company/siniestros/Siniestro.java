package com.digital_insurance_company.siniestros;

import java.util.UUID;

public class Siniestro {
    private final UUID id;
    private final String polizaId;
    private final String descripcion;
    private EstadoSiniestro estado;
    private final double valorAseguradoPoliza;

    private MontoAprobado montoAprobado;

    // Constructor paquete para la Factory
    Siniestro(UUID id, String polizaId, String descripcion, double valorAseguradoPoliza) {
        this.id = id;
        this.polizaId = polizaId;
        this.descripcion = descripcion;
        this.valorAseguradoPoliza = valorAseguradoPoliza;
        this.estado = EstadoSiniestro.REPORTADO;
    }

    public void marcarEnEvaluacion() {
        if (this.estado != EstadoSiniestro.REPORTADO) {
            throw new IllegalStateException("Solo un siniestro reportado puede pasar a evaluación");
        }
        this.estado = EstadoSiniestro.EN_EVALUACION;
    }

    public void aprobar(double valorAprobado) {
        if (this.estado != EstadoSiniestro.EN_EVALUACION) {
            throw new IllegalStateException("El siniestro debe estar en evaluación para ser aprobado");
        }
        this.montoAprobado = new MontoAprobado(valorAprobado, this.valorAseguradoPoliza);
        this.estado = EstadoSiniestro.APROBADO;
    }

    public void rechazar() {
        this.estado = EstadoSiniestro.RECHAZADO;
    }

    public UUID getId() { return id; }
    public String getPolizaId() { return polizaId; }
    public String getDescripcion() { return descripcion; }
    public EstadoSiniestro getEstado() { return estado; }
    public double getValorAseguradoPoliza() { return valorAseguradoPoliza; }
    public MontoAprobado getMontoAprobado() { return montoAprobado; }
}
