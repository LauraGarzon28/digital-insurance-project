package com.digital_insurance_company.cotizacion;

import java.math.BigDecimal;
import java.util.UUID;

public class CotizacionFactory {

    public Cotizacion crear(UUID clienteId, Riesgo riesgo) {
        if (clienteId == null) {
            throw new IllegalArgumentException("La cotización requiere un cliente");
        }
        if (riesgo == null) {
            throw new IllegalArgumentException("La cotización requiere un riesgo");
        }
        return new Cotizacion(UUID.randomUUID(), clienteId, riesgo);
    }

    public Cotizacion crear(UUID clienteId, String descripcionRiesgo,
            BigDecimal valorAsegurado, int puntajeRiesgo) {
        return crear(clienteId, new Riesgo(descripcionRiesgo, valorAsegurado, puntajeRiesgo));
    }
}
