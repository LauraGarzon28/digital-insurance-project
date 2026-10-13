package com.digital_insurance_company.poliza.dominio;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public record DatosCotizacionParaEmision(
        UUID cotizacionId,
        UUID clienteId,
        BigDecimal valorAsegurado,
        boolean aceptada
        ) {

    public DatosCotizacionParaEmision {
        Objects.requireNonNull(cotizacionId, "cotizacionId es obligatorio");
        Objects.requireNonNull(clienteId, "clienteId es obligatorio");
        Objects.requireNonNull(valorAsegurado, "valorAsegurado es obligatorio");
    }

}
