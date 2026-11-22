package com.auditoria_service;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Registro de mensajes ya procesados. Es lo que hace idempotente al consumidor:
 * un evento reentregado por el bróker no se audita dos veces.
 */
@Entity
@Table(name = "mensajes_procesados")
public class MensajeProcesado {

    @Id
    @Column(name = "mensaje_id")
    private UUID mensajeId;

    @Column(name = "procesado_en", nullable = false)
    private Instant procesadoEn;

    protected MensajeProcesado() {
    }

    public MensajeProcesado(UUID mensajeId) {
        this.mensajeId = mensajeId;
        this.procesadoEn = Instant.now();
    }

    public UUID getMensajeId() { return mensajeId; }
    public Instant getProcesadoEn() { return procesadoEn; }
}
