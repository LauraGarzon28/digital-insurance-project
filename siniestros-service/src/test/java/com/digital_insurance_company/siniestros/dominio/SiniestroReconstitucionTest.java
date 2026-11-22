package com.digital_insurance_company.siniestros.dominio;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class SiniestroReconstitucionTest {

    private static final UUID SINIESTRO_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID POLIZA_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");

    private final SiniestroFactory factory = new SiniestroFactory();

    @Test
    void reconstituyeUnSiniestroReportadoSinMonto() {
        Siniestro siniestro = factory.reconstituir(
                SINIESTRO_ID, POLIZA_ID, "Choque frontal",
                EstadoSiniestro.REPORTADO, new BigDecimal("50000"), null);

        assertEquals(SINIESTRO_ID, siniestro.getId());
        assertEquals(POLIZA_ID, siniestro.getPolizaId());
        assertEquals(EstadoSiniestro.REPORTADO, siniestro.getEstado());
        assertNull(siniestro.getMontoAprobado());
    }

    @Test
    void reconstituyeUnSiniestroAprobadoConservandoSuMonto() {
        Siniestro siniestro = factory.reconstituir(
                SINIESTRO_ID, POLIZA_ID, "Robo de equipo",
                EstadoSiniestro.APROBADO, new BigDecimal("15000"), new BigDecimal("10000"));

        assertEquals(EstadoSiniestro.APROBADO, siniestro.getEstado());
        assertEquals(new BigDecimal("10000.00"), siniestro.getMontoAprobado().valor());
    }

    @Test
    void unSiniestroReconstituidoAprobadoNoPuedeVolverAEvaluarse() {
        Siniestro siniestro = factory.reconstituir(
                SINIESTRO_ID, POLIZA_ID, "Robo de equipo",
                EstadoSiniestro.APROBADO, new BigDecimal("15000"), new BigDecimal("10000"));

        assertThrows(IllegalStateException.class, siniestro::marcarEnEvaluacion);
    }

    @Test
    void unSiniestroReconstituidoRechazadoConservaSuEstado() {
        Siniestro siniestro = factory.reconstituir(
                SINIESTRO_ID, POLIZA_ID, "Fraude",
                EstadoSiniestro.RECHAZADO, new BigDecimal("15000"), null);

        assertEquals(EstadoSiniestro.RECHAZADO, siniestro.getEstado());
    }
}
