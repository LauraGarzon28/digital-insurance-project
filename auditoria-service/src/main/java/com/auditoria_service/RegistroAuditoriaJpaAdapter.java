package com.auditoria_service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class RegistroAuditoriaJpaAdapter implements RegistroAuditoria {

    private final DecisionSiniestroRepository decisiones;
    private final MensajeProcesadoRepository mensajes;

    public RegistroAuditoriaJpaAdapter(DecisionSiniestroRepository decisiones,
            MensajeProcesadoRepository mensajes) {
        this.decisiones = decisiones;
        this.mensajes = mensajes;
    }

    @Override
    public boolean existeMensaje(UUID mensajeId) {
        return mensajes.existsById(mensajeId);
    }

    @Override
    public void registrarMensaje(UUID mensajeId) {
        mensajes.save(new MensajeProcesado(mensajeId));
    }

    @Override
    public void guardarDecision(DecisionSiniestro decision) {
        decisiones.save(decision);
    }

    @Override
    public List<DecisionSiniestro> historialDe(UUID siniestroId) {
        return decisiones.findBySiniestroIdOrderByRegistradoEnAsc(siniestroId);
    }
}
