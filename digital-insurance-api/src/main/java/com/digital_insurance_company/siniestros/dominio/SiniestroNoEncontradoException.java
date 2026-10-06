package com.digital_insurance_company.siniestros.dominio;

import java.util.UUID;

public class SiniestroNoEncontradoException extends RuntimeException {

    public SiniestroNoEncontradoException(UUID siniestroId) {
        super("No existe el siniestro " + siniestroId);
    }
}
