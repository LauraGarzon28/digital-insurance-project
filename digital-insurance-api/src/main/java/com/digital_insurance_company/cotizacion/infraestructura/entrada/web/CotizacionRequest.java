package com.digital_insurance_company.cotizacion.infraestructura.entrada.web;

import java.math.BigDecimal;
import java.util.UUID;

public record CotizacionRequest(
        UUID clienteId,
        String descripcionRiesgo,
        BigDecimal valorAsegurado,
        int puntajeRiesgo) {
}
