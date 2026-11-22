package com.auditoria_service;

import java.util.List;
import java.util.UUID;

/**
 * Puerto secundario del servicio de auditoría. Aísla la proyección de decisiones
 * de Spring Data: el servicio se prueba con un doble sin base de datos.
 */
public interface RegistroAuditoria {

    boolean existeMensaje(UUID mensajeId);

    void registrarMensaje(UUID mensajeId);

    void guardarDecision(DecisionSiniestro decision);

    List<DecisionSiniestro> historialDe(UUID siniestroId);
}
