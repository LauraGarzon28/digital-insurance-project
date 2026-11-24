package com.digital_insurance_company.siniestros.infraestructura.entrada.web;

import java.math.BigDecimal;

public record EvaluarSiniestroRequest(boolean esFraude, BigDecimal montoPropuesto) {
}
