package com.digital_insurance_company.cliente.infraestructura.entrada.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ClienteRequest(
        @NotBlank(message = "El nombre completo es obligatorio")
        String nombreCompleto,
        @NotBlank(message = "El tipo de documento es obligatorio")
        String tipoDocumento,
        @NotBlank(message = "El número de documento es obligatorio")
        String numeroDocumento,
        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo no tiene un formato válido")
        String correo,
        @NotBlank(message = "El teléfono es obligatorio")
        String telefono) {
}
