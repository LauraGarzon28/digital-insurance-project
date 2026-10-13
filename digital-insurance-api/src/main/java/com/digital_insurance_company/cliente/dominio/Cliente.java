package com.digital_insurance_company.cliente.dominio;

import java.util.UUID;

public record Cliente(
        UUID id,
        String nombreCompleto,
        String tipoDocumento,
        String numeroDocumento,
        String correo,
        String telefono) {
}
