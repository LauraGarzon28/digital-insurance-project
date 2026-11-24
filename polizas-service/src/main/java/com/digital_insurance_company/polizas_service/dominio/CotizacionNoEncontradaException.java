package com.digital_insurance_company.polizas_service.dominio;

import java.util.UUID;

public class CotizacionNoEncontradaException extends RuntimeException {

    public CotizacionNoEncontradaException(UUID cotizacionId) {
        super("No se encontró la cotización con id: " + cotizacionId);
    }
}