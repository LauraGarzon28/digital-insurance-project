package com.digital_insurance_company.cotizacion.infraestructura.entrada.web;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CotizacionRequest(
        @NotNull(message = "El cliente es obligatorio")
        UUID clienteId,
        @NotBlank(message = "La descripción del riesgo es obligatoria")
        String descripcionRiesgo,
        @NotNull(message = "El valor asegurado es obligatorio")
        @Positive(message = "El valor asegurado debe ser positivo")
        BigDecimal valorAsegurado,
        @Min(value = 0, message = "El puntaje de riesgo no puede ser negativo")
        @Max(value = 100, message = "El puntaje de riesgo no puede superar 100")
        int puntajeRiesgo) {
}
