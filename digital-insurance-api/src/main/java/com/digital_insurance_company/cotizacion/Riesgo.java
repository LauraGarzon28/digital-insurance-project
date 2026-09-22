package com.digital_insurance_company.cotizacion;

import java.math.BigDecimal;

public record Riesgo(String descripcion, BigDecimal valorAsegurado, int puntaje) {

    public Riesgo {
        if (descripcion == null || descripcion.isBlank()) {
            throw new IllegalArgumentException("El riesgo requiere una descripción");
        }
        if (valorAsegurado == null || valorAsegurado.signum() <= 0) {
            throw new IllegalArgumentException("El valor asegurado debe ser positivo");
        }
        if (puntaje < 0 || puntaje > 100) {
            throw new IllegalArgumentException("El puntaje de riesgo debe estar entre 0 y 100");
        }
    }
}