package com.digital_insurance_company.poliza.infraestructura.salida.persistencia;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "polizas")
public class PolizaJpaEntity {

    @Id
    private UUID id;
    private UUID clienteId;
    private UUID cotizacionId;
    private BigDecimal valorAsegurado;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private boolean cancelada;

    protected PolizaJpaEntity() {
        // exigido por JPA
    }

    public PolizaJpaEntity(UUID id, UUID clienteId, UUID cotizacionId, BigDecimal valorAsegurado,
            LocalDate fechaInicio, LocalDate fechaFin, boolean cancelada) {
        this.id = id;
        this.clienteId = clienteId;
        this.cotizacionId = cotizacionId;
        this.valorAsegurado = valorAsegurado;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.cancelada = cancelada;
    }

    public UUID getId() {
        return id;
    }

    public UUID getClienteId() {
        return clienteId;
    }

    public UUID getCotizacionId() {
        return cotizacionId;
    }

    public BigDecimal getValorAsegurado() {
        return valorAsegurado;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public boolean isCancelada() {
        return cancelada;
    }
}
