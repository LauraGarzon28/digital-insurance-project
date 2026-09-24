package com.digital_insurance_company.poliza;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class VigenciaPolizaTest {

    private static final LocalDate INICIO = LocalDate.of(2026, 1, 1);
    private static final LocalDate FIN = LocalDate.of(2026, 12, 31);

    @Test
    void creaVigenciaValida() {
        var v = new VigenciaPoliza(INICIO, FIN);
        assertEquals(INICIO, v.fechaInicio());
        assertEquals(FIN, v.fechaFin());
    }

    @Test
    void rechazaInicioPosteriorAlFin() {
        assertThrows(IllegalArgumentException.class, () -> new VigenciaPoliza(FIN, INICIO));
    }

    @Test
    void rechazaInicioIgualAlFin() {
        assertThrows(IllegalArgumentException.class, () -> new VigenciaPoliza(INICIO, INICIO));
    }

    @Test
    void rechazaFechasNulas() {
        assertThrows(NullPointerException.class, () -> new VigenciaPoliza(null, FIN));
        assertThrows(NullPointerException.class, () -> new VigenciaPoliza(INICIO, null));
    }

    @Test
    void cubreIncluyeExtremos() {
        var v = new VigenciaPoliza(INICIO, FIN);
        assertTrue(v.estaVigente(INICIO));
        assertTrue(v.estaVigente(FIN));
        assertFalse(v.estaVigente(INICIO.minusDays(1)));
        assertFalse(v.estaVigente(FIN.plusDays(1)));
    }

    @Test
    void venceElDiaSiguienteAlFin() {
        var v = new VigenciaPoliza(INICIO, FIN);
        assertFalse(v.haVencido(FIN));
        assertTrue(v.haVencido(FIN.plusDays(1)));
    }

    @Test
    void renovarConservaDuracionYArrancaDiaSiguiente() {
        var v = new VigenciaPoliza(INICIO, FIN);
        var r = v.renovar();
        assertEquals(FIN.plusDays(1), r.fechaInicio());
        assertEquals(java.time.temporal.ChronoUnit.DAYS.between(INICIO, FIN),
                java.time.temporal.ChronoUnit.DAYS.between(r.fechaInicio(), r.fechaFin()));
    }

    @Test
    void esInmutable() {
        var v = new VigenciaPoliza(INICIO, FIN);
        v.renovar();
        assertEquals(FIN, v.fechaFin());
    }
}
