package com.digital_insurance_company.cotizacion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.Test;

public class CotizacionFactoryTest {
    private final CotizacionFactory factory = new CotizacionFactory();

    @Test
    void creaUnaCotizacionSolicitadaConIdGenerado() {
        UUID clienteId = UUID.randomUUID();

        Cotizacion cotizacion = factory.crear(clienteId, "Vivienda", new BigDecimal("120000000"), 20);

        assertNotNull(cotizacion.id());
        assertEquals(clienteId, cotizacion.clienteId());
        assertEquals(EstadoCotizacion.SOLICITADA, cotizacion.estado());
        assertNull(cotizacion.prima());
    }

    @Test
    void rechazaCotizarSinCliente() {
        Riesgo riesgo = new Riesgo("Vivienda", BigDecimal.TEN, 0);
        assertThrows(IllegalArgumentException.class, () -> factory.crear(null, riesgo));
    }

    @Test
    void rechazaCotizarSinRiesgo() {
        assertThrows(IllegalArgumentException.class, () -> factory.crear(UUID.randomUUID(), (Riesgo) null));
    }

    @Test
    void rechazaDatosDeRiesgoInvalidos() {
        UUID clienteId = UUID.randomUUID();
        assertThrows(IllegalArgumentException.class,
                () -> factory.crear(clienteId, "Vivienda", new BigDecimal("-1"), 10));
        assertThrows(IllegalArgumentException.class,
                () -> factory.crear(clienteId, "Vivienda", BigDecimal.TEN, 150));
    }

    @Test
    void cadaCotizacionRecibeUnIdDistinto() {
        UUID clienteId = UUID.randomUUID();
        Riesgo riesgo = new Riesgo("Vivienda", BigDecimal.TEN, 0);

        assertNotEquals(factory.crear(clienteId, riesgo).id(), factory.crear(clienteId, riesgo).id());
    }
}
