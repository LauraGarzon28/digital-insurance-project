package com.cotizaciones_service.cotizacion.infraestructura.salida.persistencia;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "cotizaciones")
public class CotizacionJpaEntity {

    @Id
    private UUID id;
    @Column(name = "cliente_id", nullable = false)
    private UUID clienteId;
    @Column(name = "descripcion_riesgo", nullable = false)
    private String descripcionRiesgo;
    @Column(name = "valor_asegurado", nullable = false, precision = 18, scale = 2)
    private BigDecimal valorAsegurado;
    @Column(name = "puntaje_riesgo", nullable = false)
    private int puntajeRiesgo;
    @Column(name = "prima_monto", precision = 18, scale = 2)
    private BigDecimal primaMonto;
    @Column(name = "prima_version_reglas", length = 50)
    private String primaVersionReglas;
    @Column(nullable = false, length = 30)
    private String estado;

    protected CotizacionJpaEntity() {
    }

    public CotizacionJpaEntity(UUID id, UUID clienteId, String descripcionRiesgo,
            BigDecimal valorAsegurado, int puntajeRiesgo, BigDecimal primaMonto,
            String primaVersionReglas, String estado) {
        this.id = id;
        this.clienteId = clienteId;
        this.descripcionRiesgo = descripcionRiesgo;
        this.valorAsegurado = valorAsegurado;
        this.puntajeRiesgo = puntajeRiesgo;
        this.primaMonto = primaMonto;
        this.primaVersionReglas = primaVersionReglas;
        this.estado = estado;
    }

    public UUID getId() { return id; }
    public UUID getClienteId() { return clienteId; }
    public String getDescripcionRiesgo() { return descripcionRiesgo; }
    public BigDecimal getValorAsegurado() { return valorAsegurado; }
    public int getPuntajeRiesgo() { return puntajeRiesgo; }
    public BigDecimal getPrimaMonto() { return primaMonto; }
    public String getPrimaVersionReglas() { return primaVersionReglas; }
    public String getEstado() { return estado; }
    
}