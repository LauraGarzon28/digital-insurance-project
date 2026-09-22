package com.digital_insurance_company.cotizacion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

public class EvaluacionRiesgoServiceTest {
     private final EvaluacionRiesgoService servicio = new EvaluacionRiesgoService();
    private final ReglasSuscripcion reglas = new ReglasSuscripcion("v1", new BigDecimal("0.02"), 70);

    @Test
    void calculaLaPrimaSegunValorTasaYPuntaje() {
        Riesgo riesgo = new Riesgo("Vehículo particular", new BigDecimal("50000000"), 50);

        EvaluacionRiesgo evaluacion = servicio.evaluar(riesgo, reglas);

        // 50.000.000 × 0.02 × 1.5 = 1.500.000
        assertEquals(new BigDecimal("1500000.00"), evaluacion.prima().monto());
    }

    @Test
    void laPrimaQuedaAsociadaALaVersionDeReglasVigente() {
        Riesgo riesgo = new Riesgo("Vivienda", new BigDecimal("100000000"), 10);

        EvaluacionRiesgo evaluacion = servicio.evaluar(riesgo, reglas);

        assertEquals("v1", evaluacion.prima().versionReglas());
    }

    @Test
    void riesgoBajoElUmbralNoRequiereRevisionManual() {
        Riesgo riesgo = new Riesgo("Vivienda", new BigDecimal("100000000"), 70);

        assertFalse(servicio.evaluar(riesgo, reglas).requiereRevisionManual());
    }

    @Test
    void riesgoQueSuperaElUmbralSeMarcaParaRevisionManual() {
        Riesgo riesgo = new Riesgo("Vehículo de carga", new BigDecimal("80000000"), 71);

        assertTrue(servicio.evaluar(riesgo, reglas).requiereRevisionManual());
    }

    @Test
    void cambiarLaVersionDeReglasCambiaLaPrimaSinTocarElServicio() {
        Riesgo riesgo = new Riesgo("Vivienda", new BigDecimal("100000000"), 0);
        ReglasSuscripcion v2 = new ReglasSuscripcion("v2", new BigDecimal("0.03"), 70);

        assertEquals(new BigDecimal("2000000.00"), servicio.evaluar(riesgo, reglas).prima().monto());
        assertEquals(new BigDecimal("3000000.00"), servicio.evaluar(riesgo, v2).prima().monto());
    }

    @Test
    void rechazaEvaluarSinRiesgoOSinReglas() {
        Riesgo riesgo = new Riesgo("Vivienda", BigDecimal.TEN, 0);
        assertThrows(IllegalArgumentException.class, () -> servicio.evaluar(null, reglas));
        assertThrows(IllegalArgumentException.class, () -> servicio.evaluar(riesgo, null));
    }
}
