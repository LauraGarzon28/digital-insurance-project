package com.auditoria_service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "decisiones_siniestro")
public class DecisionSiniestro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "mensaje_id", nullable = false, unique = true)
    private UUID mensajeId;

    @Column(name = "siniestro_id", nullable = false)
    private UUID siniestroId;

    @Column(name = "poliza_id", nullable = false)
    private UUID polizaId;

    @Column(nullable = false, length = 40)
    private String tipo;

    @Column(nullable = false, length = 20)
    private String estado;

    @Column(name = "monto_aprobado", precision = 19, scale = 2)
    private BigDecimal montoAprobado;

    @Column(name = "ocurrido_en", nullable = false)
    private Instant ocurridoEn;

    @Column(name = "registrado_en", nullable = false)
    private Instant registradoEn;

    protected DecisionSiniestro() {
    }

    public static DecisionSiniestro desde(EventoSiniestro evento) {
        DecisionSiniestro decision = new DecisionSiniestro();
        decision.mensajeId = evento.mensajeId();
        decision.siniestroId = evento.siniestroId();
        decision.polizaId = evento.polizaId();
        decision.tipo = evento.tipo();
        decision.estado = evento.estado();
        decision.montoAprobado = evento.montoAprobado();
        decision.ocurridoEn = evento.ocurridoEn();
        decision.registradoEn = Instant.now();
        return decision;
    }

    public Long getId() { return id; }
    public UUID getMensajeId() { return mensajeId; }
    public UUID getSiniestroId() { return siniestroId; }
    public UUID getPolizaId() { return polizaId; }
    public String getTipo() { return tipo; }
    public String getEstado() { return estado; }
    public BigDecimal getMontoAprobado() { return montoAprobado; }
    public Instant getOcurridoEn() { return ocurridoEn; }
    public Instant getRegistradoEn() { return registradoEn; }
}
