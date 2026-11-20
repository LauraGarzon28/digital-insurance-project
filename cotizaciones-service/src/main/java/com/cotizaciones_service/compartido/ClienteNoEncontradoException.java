package com.cotizaciones_service.compartido;

import java.util.UUID;

public class ClienteNoEncontradoException extends RuntimeException {

    public ClienteNoEncontradoException(UUID id) {
        super("No existe un cliente con id " + id);
    }
}