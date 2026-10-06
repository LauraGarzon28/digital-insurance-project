package com.digital_insurance_company.poliza.dominio;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class EmisionPolizaServiceTest {

    private static final LocalDate INICIO = LocalDate.of(2026, 1, 1);
    private static final LocalDate FIN = LocalDate.of(2026, 12, 31);

    private final EmisionPolizaService servicio = new EmisionPolizaService(new PolizaFactory());

    private DatosCotizacionParaEmision datos(boolean aceptada) {
        return new DatosCotizacionParaEmision(
                UUID.randomUUID(), UUID.randomUUID(), new BigDecimal("80000000"), aceptada);
    }

    @Test
    void emiteDesdeCotizacionAceptada() {
        var d = datos(true);
        Poliza p = servicio.emitir(d, INICIO, FIN);
        assertEquals(d.cotizacionId(), p.cotizacionId());
        assertEquals(d.clienteId(), p.clienteId());
        assertEquals(EstadoPoliza.VIGENTE, p.estadoEn(INICIO));
    }

    @Test
    void noEmiteSiLaCotizacionNoEstaAceptada() {
        assertThrows(EmisionNoPermitidaException.class,
                () -> servicio.emitir(datos(false), INICIO, FIN));
    }

    @Test
    void propagaVigenciaInvalida() {
        assertThrows(IllegalArgumentException.class,
                () -> servicio.emitir(datos(true), FIN, INICIO));
    }
}
