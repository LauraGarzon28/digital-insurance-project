package com.digital_insurance_company.siniestros.infraestructura.entrada.web;

import java.math.BigDecimal;
import java.util.UUID;

public record SiniestroRequest(UUID polizaId, String descripcion, BigDecimal valorAseguradoPoliza) {
}
