package com.digital_insurance_company.polizas_service.infraestructura.entrada.web;

import java.math.BigDecimal;
import java.util.UUID;

public record SembrarCotizacionRequest(UUID clienteId, BigDecimal valorAsegurado, Boolean aceptada) {

}
