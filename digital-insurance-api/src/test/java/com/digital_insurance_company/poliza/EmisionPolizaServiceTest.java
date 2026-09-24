package com.digital_insurance_company.poliza;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import com.digital_insurance_company.cotizacion.Cotizacion;
import com.digital_insurance_company.cotizacion.CotizacionFactory;
import com.digital_insurance_company.cotizacion.EvaluacionRiesgoService;
import com.digital_insurance_company.cotizacion.ReglasSuscripcion;
import com.digital_insurance_company.cotizacion.Riesgo;

public class EmisionPolizaServiceTest {

    private static final LocalDate INICIO = LocalDate.of(2026, 1, 1);
    private static final LocalDate FIN = LocalDate.of(2026, 12, 31);

    private final EmisionPolizaService servicio = new EmisionPolizaService(new PolizaFactory());
    private final CotizacionFactory cotizacionFactory = new CotizacionFactory();
    private final EvaluacionRiesgoService evaluacionService = new EvaluacionRiesgoService();

    // Reglas con umbral 50: puntaje <= 50 aprueba directo, puntaje > 50 exige revisión manual.
    private final ReglasSuscripcion reglas = new ReglasSuscripcion("v1", new BigDecimal("0.02"), 50);

    private Cotizacion cotizacionAceptada(int puntajeRiesgo) {
        UUID clienteId = UUID.randomUUID();
        Riesgo riesgo = new Riesgo("Vivienda unifamiliar", new BigDecimal("80000000"), puntajeRiesgo);
        Cotizacion c = cotizacionFactory.crear(clienteId, riesgo);
        c.registrarEvaluacion(evaluacionService.evaluar(riesgo, reglas));
        if (c.estado() == com.digital_insurance_company.cotizacion.EstadoCotizacion.EN_REVISION_MANUAL) {
            c.aprobarRevisionManual();
        }
        c.aceptar();
        return c;
    }

    @Test
    void emiteDesdeCotizacionAceptadaSinRevisionManual() {
        Cotizacion c = cotizacionAceptada(20); // <= umbral: no requiere revisión manual
        Poliza p = servicio.emitir(c, INICIO, FIN);
        assertEquals(c.id(), p.cotizacionId());
        assertEquals(c.clienteId(), p.clienteId());
        assertEquals(c.riesgo().valorAsegurado(), p.valorAsegurado().monto());
        assertEquals(EstadoPoliza.VIGENTE, p.estadoEn(INICIO));
    }

    @Test
    void emiteDesdeCotizacionAceptadaTrasRevisionManual() {
        Cotizacion c = cotizacionAceptada(80); // > umbral: pasa por revisión manual
        Poliza p = servicio.emitir(c, INICIO, FIN);
        assertEquals(EstadoPoliza.VIGENTE, p.estadoEn(INICIO));
    }

    @Test
    void noEmiteSiLaCotizacionNoHaSidoAceptada() {
        UUID clienteId = UUID.randomUUID();
        Riesgo riesgo = new Riesgo("Vehículo", new BigDecimal("40000000"), 10);
        Cotizacion c = cotizacionFactory.crear(clienteId, riesgo);
        c.registrarEvaluacion(evaluacionService.evaluar(riesgo, reglas));
        // Nunca se llama a aceptar(): sigue en COTIZADA.

        assertThrows(EmisionNoPermitidaException.class, () -> servicio.emitir(c, INICIO, FIN));
    }

    @Test
    void noEmiteSiLaCotizacionFueRechazadaEnRevisionManual() {
        UUID clienteId = UUID.randomUUID();
        Riesgo riesgo = new Riesgo("Nave industrial", new BigDecimal("500000000"), 95);
        Cotizacion c = cotizacionFactory.crear(clienteId, riesgo);
        c.registrarEvaluacion(evaluacionService.evaluar(riesgo, reglas)); // > umbral -> EN_REVISION_MANUAL
        c.rechazar();

        assertThrows(EmisionNoPermitidaException.class, () -> servicio.emitir(c, INICIO, FIN));
    }

    @Test
    void propagaVigenciaInvalida() {
        Cotizacion c = cotizacionAceptada(10);
        assertThrows(IllegalArgumentException.class, () -> servicio.emitir(c, FIN, INICIO));
    }
}
