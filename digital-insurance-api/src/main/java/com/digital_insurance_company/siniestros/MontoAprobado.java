package com.digital_insurance_company.siniestros;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record MontoAprobado(BigDecimal valor, BigDecimal valorAseguradoPoliza) {
    public MontoAprobado {
        if (valor == null || valorAseguradoPoliza == null) {
            throw new IllegalArgumentException("El monto aprobado y el valor asegurado de la póliza son requeridos");
        }
        valor = valor.setScale(2, RoundingMode.HALF_UP); // normaliza: 500 y 500.00 son el mismo monto
        valorAseguradoPoliza = valorAseguradoPoliza.setScale(2, RoundingMode.HALF_UP);
        if (valor.signum() < 0) {
            throw new IllegalArgumentException("El monto aprobado no puede ser negativo");
        }
        if (valor.compareTo(valorAseguradoPoliza) > 0) {
            throw new IllegalArgumentException("El monto aprobado no puede superar el valor asegurado de la póliza");
        }
    }
}
