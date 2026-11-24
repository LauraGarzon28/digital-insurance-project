package com.digital_insurance_company.siniestros.dominio;

import java.util.UUID;

public class PolizaNoEncontradaException extends RuntimeException {

    public PolizaNoEncontradaException(UUID polizaId) {
        super("No existe la póliza " + polizaId);
    }
}
