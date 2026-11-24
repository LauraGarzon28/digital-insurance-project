package com.digital_insurance_company.siniestros.dominio;

import java.math.BigDecimal;
import java.util.Objects;

public record DatosPolizaParaSiniestro(BigDecimal valorAsegurado, EstadoPolizaReferencia estado) {

    public DatosPolizaParaSiniestro {
        Objects.requireNonNull(valorAsegurado, "valorAsegurado es obligatorio");
        Objects.requireNonNull(estado, "estado es obligatorio");
    }
}
