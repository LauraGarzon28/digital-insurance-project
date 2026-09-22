package com.digital_insurance_company.cotizacion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.Test;

public class CotizacionTest {

    private final CotizacionFactory factory = new CotizacionFactory();
    private final EvaluacionRiesgoService servicio = new EvaluacionRiesgoService();
    private final ReglasSuscripcion reglas = new ReglasSuscripcion("v1", new BigDecimal("0.02"), 70);

     private Cotizacion nuevaCotizacion(int puntaje) {
        return factory.crear(UUID.randomUUID(), "Vehículo particular", new BigDecimal("50000000"), puntaje);
    }


    @Test
    void riesgoBajoElUmbralQuedaCotizadaConSuPrima() {
        Cotizacion cotizacion = nuevaCotizacion(30);

        cotizacion.registrarEvaluacion(servicio.evaluar(cotizacion.riesgo(), reglas));

        assertEquals(EstadoCotizacion.COTIZADA, cotizacion.estado());
        assertEquals("v1", cotizacion.prima().versionReglas());
    }

    @Test
    void riesgoSobreElUmbralQuedaEnRevisionManual() {
        Cotizacion cotizacion = nuevaCotizacion(90);

        cotizacion.registrarEvaluacion(servicio.evaluar(cotizacion.riesgo(), reglas));

        assertEquals(EstadoCotizacion.EN_REVISION_MANUAL, cotizacion.estado());
    }

    @Test
    void elClientePuedeAceptarUnaCotizacionCotizada() {
        Cotizacion cotizacion = nuevaCotizacion(30);
        cotizacion.registrarEvaluacion(servicio.evaluar(cotizacion.riesgo(), reglas));

        cotizacion.aceptar();

        assertEquals(EstadoCotizacion.ACEPTADA, cotizacion.estado());
    }

    @Test
    void noSePuedeAceptarUnaCotizacionEnRevisionManual() {
        Cotizacion cotizacion = nuevaCotizacion(90);
        cotizacion.registrarEvaluacion(servicio.evaluar(cotizacion.riesgo(), reglas));

        assertThrows(IllegalStateException.class, cotizacion::aceptar);
    }

    @Test
    void elSuscriptorPuedeAprobarLaRevisionYLuegoElClienteAceptar() {
        Cotizacion cotizacion = nuevaCotizacion(90);
        cotizacion.registrarEvaluacion(servicio.evaluar(cotizacion.riesgo(), reglas));

        cotizacion.aprobarRevisionManual();
        cotizacion.aceptar();

        assertEquals(EstadoCotizacion.ACEPTADA, cotizacion.estado());
    }

    @Test
    void unaCotizacionRechazadaNuncaSePuedeAceptar() {
        Cotizacion cotizacion = nuevaCotizacion(90);
        cotizacion.registrarEvaluacion(servicio.evaluar(cotizacion.riesgo(), reglas));
        cotizacion.rechazar();

        assertEquals(EstadoCotizacion.RECHAZADA, cotizacion.estado());
        assertThrows(IllegalStateException.class, cotizacion::aceptar);
    }

    @Test
    void noSePuedeEvaluarDosVeces() {
        Cotizacion cotizacion = nuevaCotizacion(30);
        EvaluacionRiesgo evaluacion = servicio.evaluar(cotizacion.riesgo(), reglas);
        cotizacion.registrarEvaluacion(evaluacion);

        assertThrows(IllegalStateException.class, () -> cotizacion.registrarEvaluacion(evaluacion));
    }
}
