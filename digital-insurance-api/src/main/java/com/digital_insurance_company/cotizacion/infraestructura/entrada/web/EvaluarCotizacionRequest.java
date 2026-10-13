package com.digital_insurance_company.cotizacion.infraestructura.entrada.web;

import java.math.BigDecimal;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record EvaluarCotizacionRequest(
        @NotBlank(message = "La versión de reglas es obligatoria")
        String versionReglas,
        @NotNull(message = "La tasa base es obligatoria")
        @Positive(message = "La tasa base debe ser positiva")
        BigDecimal tasaBase,
        @Min(value = 0, message = "El umbral no puede ser negativo")
        @Max(value = 100, message = "El umbral no puede superar 100")
        int umbralRevisionManual) {
}
