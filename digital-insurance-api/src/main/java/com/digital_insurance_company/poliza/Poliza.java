package com.digital_insurance_company.poliza;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class Poliza {

    private final UUID id;
    private final UUID clienteId;
    private final UUID cotizacionId;
    private VigenciaPoliza vigencia;
    private boolean cancelada;
    
    public Poliza(UUID id, UUID clienteId, UUID cotizacionId, VigenciaPoliza vigencia, boolean cancelada) {
        this.id = UUID.randomUUID();
        this.clienteId = null;
        this.cotizacionId = null;
        this.vigencia = vigencia;
        this.cancelada = cancelada;
    }

    public EstadoPoliza estadoEn(LocalDate fecha) {
        if (cancelada) {
            return EstadoPoliza.CANCELADA;
        }
        return vigencia.haVencido(fecha) ? EstadoPoliza.VENCIDA : EstadoPoliza.VIGENTE;
    }

    public boolean estaVigenteEn(LocalDate fecha) {
        return estadoEn(fecha) == EstadoPoliza.VIGENTE && vigencia.estaVigente(fecha);
    }

    public void renovar(){
        if (cancelada) {
            throw new IllegalStateException("No se puede renovar una póliza cancelada");
        }
        this.vigencia = vigencia.renovar();
    }

    public void cancelar() {
        if (cancelada) {
            throw new IllegalStateException("La póliza ya se encuentra cancelada");
        }
        this.cancelada = true;
    }

    public UUID id() { return id; }
    public UUID clienteId() { return clienteId; }
    public UUID cotizacionId() { return cotizacionId; }

    @Override 
    public boolean equals(Object o){
        return o instanceof Poliza otra && id.equals(otra.id);
    }

    @Override 
    public int hashCode(){
        return Objects.hash(id);
    }
}
