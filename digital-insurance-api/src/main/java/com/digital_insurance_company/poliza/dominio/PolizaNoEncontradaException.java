package com.digital_insurance_company.poliza.dominio;

import java.util.UUID;

public class PolizaNoEncontradaException extends RuntimeException {

    public PolizaNoEncontradaException(UUID id) {
        super("No existe una póliza con id " + id);
    }

}
