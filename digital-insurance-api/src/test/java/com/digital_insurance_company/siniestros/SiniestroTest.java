package com.digital_insurance_company.siniestros;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SiniestroTest {

    private static final UUID POLIZA_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    private Siniestro reportar(String descripcion, String valorAsegurado) {
        return new SiniestroFactory().reportarSiniestro(POLIZA_ID, descripcion, new BigDecimal(valorAsegurado));
    }

    @Test
    void debeValidarValueObjectMontoAprobado() {
        assertThrows(IllegalArgumentException.class, () -> new MontoAprobado(new BigDecimal("-100"), new BigDecimal("1000")));
        assertThrows(IllegalArgumentException.class, () -> new MontoAprobado(new BigDecimal("1500"), new BigDecimal("1000")));
        assertDoesNotThrow(() -> new MontoAprobado(new BigDecimal("500"), new BigDecimal("1000")));
    }

    @Test
    void montoAprobadoDebeNormalizarLaEscala() {
        assertEquals(new BigDecimal("500.00"), new MontoAprobado(new BigDecimal("500"), new BigDecimal("1000")).valor());
    }

    @Test
    void factoryDebeCrearSiniestroReportado() {
        Siniestro siniestro = reportar("Choque frontal", "50000");

        assertNotNull(siniestro);
        assertEquals(EstadoSiniestro.REPORTADO, siniestro.getEstado());
        assertEquals(POLIZA_ID, siniestro.getPolizaId());
    }

    @Test
    void factoryDebeRechazarDatosInvalidos() {
        SiniestroFactory factory = new SiniestroFactory();

        assertThrows(IllegalArgumentException.class, () -> factory.reportarSiniestro(null, "Choque", new BigDecimal("1000")));
        assertThrows(IllegalArgumentException.class, () -> factory.reportarSiniestro(POLIZA_ID, "  ", new BigDecimal("1000")));
        assertThrows(IllegalArgumentException.class, () -> factory.reportarSiniestro(POLIZA_ID, "Choque", new BigDecimal("0")));
    }

    @Test
    void servicioDebeRechazarSiniestroSiPolizaVencida() {
        Siniestro siniestro = reportar("Daños por agua", "20000");

        new EvaluacionSiniestroService().evaluar(siniestro, EstadoPolizaReferencia.VENCIDA, false, new BigDecimal("5000"));

        assertEquals(EstadoSiniestro.RECHAZADO, siniestro.getEstado());
    }

    @Test
    void servicioDebeRechazarSiniestroSiPolizaCancelada() {
        Siniestro siniestro = reportar("Daños por agua", "20000");

        new EvaluacionSiniestroService().evaluar(siniestro, EstadoPolizaReferencia.CANCELADA, false, new BigDecimal("5000"));

        assertEquals(EstadoSiniestro.RECHAZADO, siniestro.getEstado());
    }

    @Test
    void servicioDebeRechazarSiniestroSiEsFraude() {
        Siniestro siniestro = reportar("Robo de equipo", "15000");

        new EvaluacionSiniestroService().evaluar(siniestro, EstadoPolizaReferencia.ACTIVA, true, new BigDecimal("10000"));

        assertEquals(EstadoSiniestro.RECHAZADO, siniestro.getEstado());
    }

    @Test
    void servicioDebeAprobarSiniestroSiCumpleReglas() {
        Siniestro siniestro = reportar("Robo de equipo", "15000");

        new EvaluacionSiniestroService().evaluar(siniestro, EstadoPolizaReferencia.ACTIVA, false, new BigDecimal("10000"));

        assertEquals(EstadoSiniestro.APROBADO, siniestro.getEstado());
        assertNotNull(siniestro.getMontoAprobado());
        assertEquals(new BigDecimal("10000.00"), siniestro.getMontoAprobado().valor());
    }

    @Test
    void servicioDebeRechazarMontoQueSuperaElValorAsegurado() {
        Siniestro siniestro = reportar("Robo de equipo", "15000");

        assertThrows(IllegalArgumentException.class, () -> new EvaluacionSiniestroService()
                .evaluar(siniestro, EstadoPolizaReferencia.ACTIVA, false, new BigDecimal("20000")));
    }

    @Test
    void rechazarSoloEsValidoDesdeEnEvaluacion() {
        Siniestro siniestro = reportar("Choque frontal", "50000");

        assertThrows(IllegalStateException.class, siniestro::rechazar);

        siniestro.marcarEnEvaluacion();
        siniestro.rechazar();

        assertEquals(EstadoSiniestro.RECHAZADO, siniestro.getEstado());
        assertThrows(IllegalStateException.class, siniestro::rechazar);
    }

    @Test
    void unSiniestroAprobadoNoPuedeRechazarse() {
        Siniestro siniestro = reportar("Choque frontal", "50000");

        siniestro.marcarEnEvaluacion();
        siniestro.aprobar(new BigDecimal("10000"));

        assertThrows(IllegalStateException.class, siniestro::rechazar);
        assertEquals(EstadoSiniestro.APROBADO, siniestro.getEstado());
    }
}
