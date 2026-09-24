package com.digital_insurance_company.poliza;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

public class PolizaFactoryTest {

     private final PolizaFactory factory = new PolizaFactory();
    private final UUID cliente = UUID.randomUUID();
    private final UUID cotizacion = UUID.randomUUID();
    private final LocalDate inicio = LocalDate.of(2026, 1, 1);
    private final LocalDate fin = LocalDate.of(2026, 12, 31);

    @Test
    void creaPolizaVigenteConDatosValidos() {
        Poliza p = factory.nueva(cliente, cotizacion, new BigDecimal("50000000"), inicio, fin);
        assertNotNull(p.id());
        assertEquals(cliente, p.clienteId());
        assertEquals(cotizacion, p.cotizacionId());
        assertEquals(EstadoPoliza.VIGENTE, p.estadoEn(inicio));
    }

    @Test
    void rechazaValorAseguradoNoPositivo() {
        assertThrows(IllegalArgumentException.class,
                () -> factory.nueva(cliente, cotizacion, BigDecimal.ZERO, inicio, fin));
    }

    @Test
    void rechazaVigenciaInvalida() {
        assertThrows(IllegalArgumentException.class,
                () -> factory.nueva(cliente, cotizacion, BigDecimal.TEN, fin, inicio));
    }

    @Test
    void rechazaReferenciasNulas() {
        assertThrows(NullPointerException.class,
                () -> factory.nueva(null, cotizacion, BigDecimal.TEN, inicio, fin));
        assertThrows(NullPointerException.class,
                () -> factory.nueva(cliente, null, BigDecimal.TEN, inicio, fin));
    }

    @Test
    void reconstruyePolizaCancelada() {
        Poliza p = factory.reconstruir(UUID.randomUUID(), cliente, cotizacion,
                BigDecimal.TEN, inicio, fin, true);
        assertEquals(EstadoPoliza.CANCELADA, p.estadoEn(inicio));
    }
}
