package com.digital_insurance_company.cotizacion.aplicacion;

import java.util.UUID;

public class CotizacionNoEncontradaException extends RuntimeException {

    public CotizacionNoEncontradaException(UUID id) {
        super("No existe una cotización con id " + id);
    }
}
