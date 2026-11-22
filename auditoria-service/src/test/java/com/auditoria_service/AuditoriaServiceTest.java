package com.auditoria_service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * La prueba central del consumidor: un mismo mensaje entregado dos veces solo deja
 * una decisión auditada. Se usa un fake del puerto, sin base de datos ni bróker.
 */
class AuditoriaServiceTest {

    private static class RegistroAuditoriaFake implements RegistroAuditoria {

        final Set<UUID> mensajes = new HashSet<>();
        final List<DecisionSiniestro> decisiones = new ArrayList<>();

        @Override
        public boolean existeMensaje(UUID mensajeId) {
            return mensajes.contains(mensajeId);
        }

        @Override
        public void registrarMensaje(UUID mensajeId) {
            mensajes.add(mensajeId);
        }

        @Override
        public void guardarDecision(DecisionSiniestro decision) {
            decisiones.add(decision);
        }

        @Override
        public List<DecisionSiniestro> historialDe(UUID siniestroId) {
            return decisiones.stream()
                    .filter(decision -> decision.getSiniestroId().equals(siniestroId))
                    .toList();
        }
    }

    private static final UUID SINIESTRO_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID POLIZA_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");

    private final RegistroAuditoriaFake registro = new RegistroAuditoriaFake();
    private final AuditoriaService servicio = new AuditoriaService(registro);

    private EventoSiniestro evento(UUID mensajeId, String tipo, String estado, BigDecimal monto) {
        return new EventoSiniestro(mensajeId, tipo, SINIESTRO_ID, POLIZA_ID, estado, monto, Instant.now());
    }

    @Test
    void registraUnEventoNuevo() {
        boolean registrado = servicio.registrar(
                evento(UUID.randomUUID(), "SiniestroReportado", "REPORTADO", null));

        assertTrue(registrado);
        assertEquals(1, registro.decisiones.size());
    }

    @Test
    void elMismoMensajeDosVecesSoloSeAuditaUnaVez() {
        UUID mensajeId = UUID.randomUUID();
        EventoSiniestro original = evento(mensajeId, "SiniestroResuelto", "APROBADO", new BigDecimal("10000.00"));

        boolean primera = servicio.registrar(original);
        boolean segunda = servicio.registrar(original);

        assertTrue(primera);
        assertFalse(segunda);
        assertEquals(1, registro.decisiones.size());
        assertEquals(1, registro.mensajes.size());
    }

    @Test
    void dosMensajesDistintosDelMismoSiniestroSeAuditanLosDos() {
        servicio.registrar(evento(UUID.randomUUID(), "SiniestroReportado", "REPORTADO", null));
        servicio.registrar(evento(UUID.randomUUID(), "SiniestroResuelto", "APROBADO", new BigDecimal("10000.00")));

        assertEquals(2, registro.historialDe(SINIESTRO_ID).size());
    }

    @Test
    void unEventoSinMensajeIdNoPuedeSerIdempotente() {
        EventoSiniestro sinId = new EventoSiniestro(
                null, "SiniestroReportado", SINIESTRO_ID, POLIZA_ID, "REPORTADO", null, Instant.now());

        assertThrows(IllegalArgumentException.class, () -> servicio.registrar(sinId));
    }
}
