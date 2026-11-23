package com.digital_insurance_company.polizas_service.dominio;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class PolizaTest {

    private static final LocalDate INICIO = LocalDate.of(2026, 1, 1);
    private static final LocalDate FIN = LocalDate.of(2026, 12, 31);

    private Poliza nueva() {
        return new PolizaFactory().nueva(UUID.randomUUID(), UUID.randomUUID(),
                new BigDecimal("100000"), INICIO, FIN);
    }

    @Test
    void estaVigenteDentroDeLaVigencia() {
        assertEquals(EstadoPoliza.VIGENTE, nueva().estadoEn(LocalDate.of(2026, 6, 1)));
    }

    @Test
    void canceladaNoEstaVigente() {
        Poliza p = nueva();
        p.iniciarCancelacion();
        assertFalse(p.estaVigenteEn(LocalDate.of(2026, 6, 1)));

        p.confirmarCancelacion();
        assertEquals(EstadoPoliza.CANCELADA, p.estadoEn(LocalDate.of(2026, 6, 1)));
        assertFalse(p.estaVigenteEn(LocalDate.of(2026, 6, 1)));
    }

    @Test
    void noSePuedeCancelarDosVeces() {
        Poliza p = nueva();
        p.iniciarCancelacion();
        assertThrows(IllegalStateException.class, p::iniciarCancelacion);

        p.confirmarCancelacion();
        assertThrows(IllegalStateException.class, p::iniciarCancelacion);
    }

    @Test
    void polizaCanceladaNoPuedeRenovarse() {
        Poliza p = nueva();
        p.iniciarCancelacion();
        assertThrows(IllegalStateException.class, p::renovar);

        p.confirmarCancelacion();
        assertThrows(IllegalStateException.class, p::renovar);
    }

    @Test
    void revertirCancelacionDevuelvePolizaAVigente() {
        Poliza p = nueva();
        p.iniciarCancelacion();
        p.revertirCancelacion();

        assertEquals(EstadoPoliza.VIGENTE, p.estadoEn(LocalDate.of(2026, 6, 1)));
        assertTrue(p.estaVigenteEn(LocalDate.of(2026, 6, 1)));
    }
}