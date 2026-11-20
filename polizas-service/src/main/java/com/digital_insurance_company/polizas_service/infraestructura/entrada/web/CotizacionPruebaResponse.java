package com.digital_insurance_company.polizas_service.infraestructura.entrada.web;

import java.math.BigDecimal;
import java.util.UUID;

public record CotizacionPruebaResponse(
        UUID cotizacionId, UUID clienteId, BigDecimal valorAsegurado, boolean aceptada) {

}
