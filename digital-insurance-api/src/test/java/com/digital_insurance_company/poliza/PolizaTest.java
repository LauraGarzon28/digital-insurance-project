package com.digital_insurance_company.poliza;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

public class PolizaTest {

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
    void quedaVencidaTrasElFin() {
        Poliza p = nueva();
        assertEquals(EstadoPoliza.VENCIDA, p.estadoEn(FIN.plusDays(1)));
        assertFalse(p.estaVigenteEn(FIN.plusDays(1)));
    }

    @Test
    void canceladaNoEstaVigente() {
        Poliza p = nueva();
        p.cancelar();
        assertEquals(EstadoPoliza.CANCELADA, p.estadoEn(LocalDate.of(2026, 6, 1)));
        assertFalse(p.estaVigenteEn(LocalDate.of(2026, 6, 1)));
    }

    @Test
    void noSePuedeCancelarDosVeces() {
        Poliza p = nueva();
        p.cancelar();
        assertThrows(IllegalStateException.class, p::cancelar);
    }

    @Test
    void renovarExtiendeLaVigencia() {
        Poliza p = nueva();
        p.renovar();
        assertEquals(FIN.plusDays(1), p.vigencia().fechaInicio());
        assertEquals(EstadoPoliza.VIGENTE, p.estadoEn(FIN.plusDays(30)));
    }

    @Test
    void polizaCanceladaNoPuedeRenovarse() {
        Poliza p = nueva();
        p.cancelar();
        assertThrows(IllegalStateException.class, p::renovar);
    }
}
