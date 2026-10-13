package com.digital_insurance_company.cliente.aplicacion;

import java.util.UUID;

public class ClienteNoEncontradoException extends RuntimeException {

    public ClienteNoEncontradoException(UUID id) {
        super("No existe un cliente con id " + id);
    }
}
