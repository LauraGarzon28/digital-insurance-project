package com.digital_insurance_company.cliente.dominio;

import java.util.UUID;

public class ClienteFactory {

    public Cliente crear(String nombreCompleto, String tipoDocumento,
            String numeroDocumento, String correo, String telefono) {
        return new Cliente(
                UUID.randomUUID(),
                nombreCompleto,
                tipoDocumento,
                numeroDocumento,
                correo,
                telefono);
    }
}
