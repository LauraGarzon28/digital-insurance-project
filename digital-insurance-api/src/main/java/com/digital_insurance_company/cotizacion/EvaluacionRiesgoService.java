package com.digital_insurance_company.cotizacion;

import java.math.BigDecimal;

public class EvaluacionRiesgoService {
    
    private static final BigDecimal CIEN = BigDecimal.valueOf(100);

    public EvaluacionRiesgo evaluar(Riesgo riesgo, ReglasSuscripcion reglas) {
        if (riesgo == null || reglas == null) {
            throw new IllegalArgumentException("Se requiere riesgo y reglas de suscripción");
        }
        
        // prima = valor asegurado × tasa base × (1 + puntaje/100)
        BigDecimal recargo = BigDecimal.ONE.add(BigDecimal.valueOf(riesgo.puntaje()).divide(CIEN));
        BigDecimal monto = riesgo.valorAsegurado().multiply(reglas.tasaBase()).multiply(recargo);

        boolean revisionManual = riesgo.puntaje() > reglas.umbralRevisionManual();
        return new EvaluacionRiesgo(new Prima(monto, reglas.version()), revisionManual);
    }
}
