package com.digital_insurance_company.siniestros.infraestructura.entrada.web;

import java.math.BigDecimal;

import com.digital_insurance_company.siniestros.dominio.EstadoPolizaReferencia;

public record EvaluarSiniestroRequest(EstadoPolizaReferencia estadoPoliza, boolean esFraude,
        BigDecimal montoPropuesto) {
}
