package com.digital_insurance_company.cliente.aplicacion;

public class ClienteYaRegistradoException extends RuntimeException {

    public ClienteYaRegistradoException(String numeroDocumento) {
        super("Ya existe un cliente con número de documento " + numeroDocumento);
    }
}
