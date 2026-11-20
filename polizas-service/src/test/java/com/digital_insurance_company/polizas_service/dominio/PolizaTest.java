package com.digital_insurance_company.polizas_service.dominio;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
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
        p.cancelar();
        assertFalse(p.estaVigenteEn(LocalDate.of(2026, 6, 1)));
    }

    @Test
    void noSePuedeCancelarDosVeces() {
        Poliza p = nueva();
        p.cancelar();
        assertThrows(IllegalStateException.class, p::cancelar);
    }

    @Test
    void polizaCanceladaNoPuedeRenovarse() {
        Poliza p = nueva();
        p.cancelar();
        assertThrows(IllegalStateException.class, p::renovar);
    }
}
