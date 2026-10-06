package com.digital_insurance_company.siniestros.infraestructura.entrada.web;

import java.math.BigDecimal;
import java.util.UUID;

import com.digital_insurance_company.siniestros.dominio.EstadoSiniestro;
import com.digital_insurance_company.siniestros.dominio.Siniestro;

public record SiniestroResponse(UUID id,
        UUID polizaId,
        String descripcion,
        EstadoSiniestro estado,
        BigDecimal valorAseguradoPoliza,
        BigDecimal montoAprobado) {

    public static SiniestroResponse desde(Siniestro siniestro) {
        return new SiniestroResponse(
                siniestro.getId(),
                siniestro.getPolizaId(),
                siniestro.getDescripcion(),
                siniestro.getEstado(),
                siniestro.getValorAseguradoPoliza(),
                siniestro.getMontoAprobado() == null ? null : siniestro.getMontoAprobado().valor());
    }
}
