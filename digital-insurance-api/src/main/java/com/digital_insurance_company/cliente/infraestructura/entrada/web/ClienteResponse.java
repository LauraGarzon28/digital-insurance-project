package com.digital_insurance_company.cliente.infraestructura.entrada.web;

import java.util.UUID;

import com.digital_insurance_company.cliente.dominio.Cliente;

public record ClienteResponse(
        UUID id,
        String nombreCompleto,
        String tipoDocumento,
        String numeroDocumento,
        String correo,
        String telefono) {

    public static ClienteResponse desde(Cliente cliente) {
        return new ClienteResponse(
                cliente.id(),
                cliente.nombreCompleto(),
                cliente.tipoDocumento(),
                cliente.numeroDocumento(),
                cliente.correo(),
                cliente.telefono());
    }
}
