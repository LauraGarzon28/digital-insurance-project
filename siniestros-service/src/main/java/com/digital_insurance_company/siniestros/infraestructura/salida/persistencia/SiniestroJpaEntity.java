package com.digital_insurance_company.siniestros.infraestructura.salida.persistencia;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.digital_insurance_company.siniestros.dominio.EstadoSiniestro;
import com.digital_insurance_company.siniestros.dominio.Siniestro;

@Entity
@Table(name = "siniestros")
public class SiniestroJpaEntity {

    @Id
    private UUID id;

    @Column(name = "poliza_id", nullable = false)
    private UUID polizaId;

    @Column(nullable = false, length = 500)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoSiniestro estado;

    @Column(name = "valor_asegurado_poliza", nullable = false, precision = 19, scale = 2)
    private BigDecimal valorAseguradoPoliza;

    @Column(name = "monto_aprobado", precision = 19, scale = 2)
    private BigDecimal montoAprobado;

    @Column(name = "ajustador_id")
    private UUID ajustadorId;

    protected SiniestroJpaEntity() {
    }

    public static SiniestroJpaEntity desde(Siniestro siniestro) {
        SiniestroJpaEntity entidad = new SiniestroJpaEntity();
        entidad.id = siniestro.getId();
        entidad.polizaId = siniestro.getPolizaId();
        entidad.descripcion = siniestro.getDescripcion();
        entidad.estado = siniestro.getEstado();
        entidad.valorAseguradoPoliza = siniestro.getValorAseguradoPoliza();
        entidad.montoAprobado = siniestro.getMontoAprobado() == null
                ? null
                : siniestro.getMontoAprobado().valor();
        entidad.ajustadorId = siniestro.getAjustadorId();
        return entidad;
    }

    public UUID getId() { return id; }
    public UUID getPolizaId() { return polizaId; }
    public String getDescripcion() { return descripcion; }
    public EstadoSiniestro getEstado() { return estado; }
    public BigDecimal getValorAseguradoPoliza() { return valorAseguradoPoliza; }
    public BigDecimal getMontoAprobado() { return montoAprobado; }
    public UUID getAjustadorId() { return ajustadorId; }
}
