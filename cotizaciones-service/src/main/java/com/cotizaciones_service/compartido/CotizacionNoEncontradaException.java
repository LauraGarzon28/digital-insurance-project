package com.cotizaciones_service.compartido;

import java.util.UUID;

public class CotizacionNoEncontradaException extends RuntimeException {

    public CotizacionNoEncontradaException(UUID id) {
        super("No existe una cotización con id " + id);
    }
}
