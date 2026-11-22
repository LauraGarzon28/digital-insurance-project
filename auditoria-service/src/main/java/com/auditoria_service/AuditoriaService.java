package com.auditoria_service;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Proyección de los hechos de Siniestros. La idempotencia se garantiza en dos
 * niveles: el registro en {@code mensajes_procesados} y la restricción unique
 * sobre {@code decisiones_siniestro.mensaje_id}.
 */
@Service
public class AuditoriaService {

    private final RegistroAuditoria registro;

    public AuditoriaService(RegistroAuditoria registro) {
        this.registro = Objects.requireNonNull(registro);
    }

    /**
     * Registra el evento. Si el {@code mensajeId} ya fue procesado, no hace nada:
     * es el punto donde el consumidor se vuelve idempotente.
     *
     * @return {@code true} si el evento era nuevo y se registró
     */
    @Transactional
    public boolean registrar(EventoSiniestro evento) {
        if (evento == null || evento.mensajeId() == null) {
            throw new IllegalArgumentException("El evento requiere un mensajeId para poder ser idempotente");
        }
        if (registro.existeMensaje(evento.mensajeId())) {
            return false;
        }

        registro.registrarMensaje(evento.mensajeId());
        registro.guardarDecision(DecisionSiniestro.desde(evento));
        return true;
    }
}
