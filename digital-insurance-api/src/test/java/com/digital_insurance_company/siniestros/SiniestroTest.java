package com.digital_insurance_company.siniestros;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SiniestroTest {

    @Test
    void debeValidarValueObjectMontoAprobado() {
        assertThrows(IllegalArgumentException.class, () -> new MontoAprobado(-100, 1000));
        assertThrows(IllegalArgumentException.class, () -> new MontoAprobado(1500, 1000));
        assertDoesNotThrow(() -> new MontoAprobado(500, 1000));
    }

    @Test
    void factoryDebeCrearSiniestroReportado() {
        SiniestroFactory factory = new SiniestroFactory();
        Siniestro siniestro = factory.reportarSiniestro("poliza-123", "Choque frontal", 50000.0);
        
        assertNotNull(siniestro);
        assertEquals(EstadoSiniestro.REPORTADO, siniestro.getEstado());
        assertEquals("poliza-123", siniestro.getPolizaId());
    }

    @Test
    void servicioDebeRechazarSiniestroSiPolizaVencida() {
        SiniestroFactory factory = new SiniestroFactory();
        Siniestro siniestro = factory.reportarSiniestro("poliza-123", "Daños por agua", 20000.0);
        
        EvaluacionSiniestroService servicio = new EvaluacionSiniestroService();
        servicio.evaluar(siniestro, EstadoPolizaReferencia.VENCIDA, false, 5000.0);
        
        assertEquals(EstadoSiniestro.RECHAZADO, siniestro.getEstado());
    }

    @Test
    void servicioDebeAprobarSiniestroSiCumpleReglas() {
        SiniestroFactory factory = new SiniestroFactory();
        Siniestro siniestro = factory.reportarSiniestro("poliza-123", "Robo de equipo", 15000.0);
        
        EvaluacionSiniestroService servicio = new EvaluacionSiniestroService();
        servicio.evaluar(siniestro, EstadoPolizaReferencia.ACTIVA, false, 10000.0);
        
        assertEquals(EstadoSiniestro.APROBADO, siniestro.getEstado());
        assertNotNull(siniestro.getMontoAprobado());
        assertEquals(10000.0, siniestro.getMontoAprobado().valor());
    }
}
