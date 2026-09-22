package com.digital_insurance_company.cotizacion;

import java.math.BigDecimal;

public record ReglasSuscripcion(String version, BigDecimal tasaBase, int umbralRevisionManual) {

    public ReglasSuscripcion {
        if (version == null || version.isBlank()) {
            throw new IllegalArgumentException("Las reglas requieren una versión");
        }
        if (tasaBase == null || tasaBase.signum() <= 0) {
            throw new IllegalArgumentException("La tasa base debe ser positiva");
        }
        if (umbralRevisionManual < 0 || umbralRevisionManual > 100) {
            throw new IllegalArgumentException("El umbral debe estar entre 0 y 100");
        }
    }
}