package com.cotizaciones_service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.cotizaciones_service.cotizacion.dominio.Cotizacion;
import com.cotizaciones_service.cotizacion.dominio.EstadoCotizacion;
import com.cotizaciones_service.cotizacion.dominio.Prima;
import com.cotizaciones_service.cotizacion.dominio.Riesgo;

class CotizacionReconstitucionTest {

    @Test
    void reconstruyeUnaCotizacionYaCotizadaSinRepetirTransiciones() {
        Riesgo riesgo = new Riesgo("Vivienda", new BigDecimal("100000000"), 30);
        Prima prima = new Prima(new BigDecimal("2000000"), "v1");

        Cotizacion cotizacion = Cotizacion.reconstituir(
                UUID.randomUUID(), UUID.randomUUID(), riesgo, prima, EstadoCotizacion.COTIZADA);

        assertEquals(EstadoCotizacion.COTIZADA, cotizacion.estado());
        assertEquals(prima, cotizacion.prima());

        cotizacion.aceptar(); 
        assertEquals(EstadoCotizacion.ACEPTADA, cotizacion.estado());
    }
}