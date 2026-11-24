package com.digital_insurance_company.polizas_service.dominio;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class VigenciaPolizaTest {

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
        var exception = assertThrows(IllegalArgumentException.class, () -> new VigenciaPoliza(FIN, INICIO));
        assertNotNull(exception);
    }

    @Test
    void estaVigenteIncluyeExtremos() {
        var v = new VigenciaPoliza(INICIO, FIN);
        assertTrue(v.estaVigente(INICIO));
        assertTrue(v.estaVigente(FIN));
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
    }
}
