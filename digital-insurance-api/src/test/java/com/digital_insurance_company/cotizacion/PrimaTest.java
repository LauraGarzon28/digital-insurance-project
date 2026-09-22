package com.digital_insurance_company.cotizacion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

public class PrimaTest {

    @Test
    void creaPrimaValidaConVersionDeReglas() {
        Prima prima = new Prima(new BigDecimal("1500.50"), "v1");
        assertEquals(new BigDecimal("1500.50"), prima.monto());
        assertEquals("v1", prima.versionReglas());
    }

    @Test
    void rechazaMontoCeroONegativo() {
        assertThrows(IllegalArgumentException.class, () -> new Prima(BigDecimal.ZERO, "v1"));
        assertThrows(IllegalArgumentException.class, () -> new Prima(new BigDecimal("-10"), "v1"));
    }

    @Test
    void rechazaMontoNulo() {
        assertThrows(IllegalArgumentException.class, () -> new Prima(null, "v1"));
    }

    @Test
    void rechazaPrimaSinVersionDeReglas() {
        assertThrows(IllegalArgumentException.class, () -> new Prima(BigDecimal.TEN, null));
        assertThrows(IllegalArgumentException.class, () -> new Prima(BigDecimal.TEN, "  "));
    }

    @Test
    void dosPrimasConMismoMontoYVersionSonIguales() {
        assertEquals(new Prima(new BigDecimal("100"), "v1"), new Prima(new BigDecimal("100.00"), "v1"));
    }
    
}