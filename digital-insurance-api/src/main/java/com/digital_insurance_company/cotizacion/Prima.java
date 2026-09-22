package com.digital_insurance_company.cotizacion;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record Prima(BigDecimal monto, String versionReglas) {
     public Prima {
        if (monto == null) {
            throw new IllegalArgumentException("La prima requiere un monto");
        }
        monto = monto.setScale(2, RoundingMode.HALF_UP); // normaliza: 100 y 100.00 son la misma prima
        if (monto.signum() <= 0) {
            throw new IllegalArgumentException("La prima debe ser positiva");
        }
        if (versionReglas == null || versionReglas.isBlank()) {
            throw new IllegalArgumentException("La prima debe indicar la versión de reglas vigente");
        }
    }
}
