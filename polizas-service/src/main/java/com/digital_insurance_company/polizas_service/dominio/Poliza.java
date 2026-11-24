package com.digital_insurance_company.polizas_service.dominio;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class Poliza {

    private final UUID id;
    private final UUID clienteId;
    private final UUID cotizacionId;
    private final ValorAsegurado valorAsegurado;
    private VigenciaPoliza vigencia;
    private boolean cancelada;
    private boolean enCancelacion;

   public Poliza(UUID id, UUID clienteId, UUID cotizacionId, ValorAsegurado valorAsegurado,
            VigenciaPoliza vigencia, boolean cancelada, boolean enCancelacion) {
        this.id = id;
        this.clienteId = clienteId;
        this.cotizacionId = cotizacionId;
        this.valorAsegurado = valorAsegurado;
        this.vigencia = vigencia;
        this.cancelada = cancelada;
        this.enCancelacion = enCancelacion;
    }

    public EstadoPoliza estadoEn(LocalDate fecha) {
        if (cancelada) {
            return EstadoPoliza.CANCELADA;
        }
        if (enCancelacion) {
            return EstadoPoliza.EN_CANCELACION;
        }
        return vigencia.haVencido(fecha) ? EstadoPoliza.VENCIDA : EstadoPoliza.VIGENTE;
    }

    /**
     * Una póliza EN_CANCELACION ya no está vigente para efectos de reportar
     * siniestros nuevos.
     */
    public boolean estaVigenteEn(LocalDate fecha) {
        return estadoEn(fecha) == EstadoPoliza.VIGENTE && vigencia.estaVigente(fecha);
    }

    public void renovar() {
        if (cancelada) {
            throw new IllegalStateException("No se puede renovar una póliza cancelada");
        }
        if (enCancelacion) {
            throw new IllegalStateException("No se puede renovar una póliza con la cancelación en curso");
        }
        this.vigencia = vigencia.renovar();
    }

    public void iniciarCancelacion() {
        if (cancelada) {
            throw new IllegalStateException("La póliza ya se encuentra cancelada");
        }
        if (enCancelacion) {
            throw new IllegalStateException("La cancelación de la póliza ya está en curso");
        }
        this.enCancelacion = true;
    }

    public void confirmarCancelacion() {
        exigirEnCancelacion();
        this.enCancelacion = false;
        this.cancelada = true;
    }

    public void revertirCancelacion() {
        exigirEnCancelacion();
        this.enCancelacion = false;
    }

    private void exigirEnCancelacion() {
        if (!enCancelacion) {
            throw new IllegalStateException("La póliza no tiene una cancelación en curso");
        }
    }

    public UUID id() {
        return id;
    }

    public UUID clienteId() {
        return clienteId;
    }

    public UUID cotizacionId() {
        return cotizacionId;
    }

    public ValorAsegurado valorAsegurado() {
        return valorAsegurado;
    }

    public VigenciaPoliza vigencia() {
        return vigencia;
    }

    public boolean cancelada() {
        return cancelada;
    }

    public boolean enCancelacion() {
        return enCancelacion;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Poliza otra && id.equals(otra.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
