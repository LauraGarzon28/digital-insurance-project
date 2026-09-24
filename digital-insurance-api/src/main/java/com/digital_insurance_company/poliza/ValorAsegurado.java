package com.digital_insurance_company.poliza;

import java.math.BigDecimal;
import java.util.Objects;

public record ValorAsegurado(BigDecimal monto) {

    public ValorAsegurado {
        Objects.requireNonNull(monto, "El valor asegurado es obligatorio");
        if (monto.signum() <= 0) {
            throw new IllegalArgumentException("El valor asegurado debe ser positivo");
        }
    }

    public boolean cubre(BigDecimal montoReclamado) {
        return montoReclamado.compareTo(monto) <= 0;
    }

}
