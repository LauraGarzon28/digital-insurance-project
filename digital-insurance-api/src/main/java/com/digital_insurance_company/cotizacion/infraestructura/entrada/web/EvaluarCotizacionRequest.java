package com.digital_insurance_company.cotizacion.infraestructura.entrada.web;

import java.math.BigDecimal;

public record EvaluarCotizacionRequest(
        String versionReglas,
        BigDecimal tasaBase,
        int umbralRevisionManual) {
}
