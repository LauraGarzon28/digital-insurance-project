package com.digital_insurance_company.cotizacion;

public record EvaluacionRiesgo(Prima prima, boolean requiereRevisionManual) {

    public EvaluacionRiesgo {
        if (prima == null) {
            throw new IllegalArgumentException("La evaluación requiere una prima");
        }
    }
}
