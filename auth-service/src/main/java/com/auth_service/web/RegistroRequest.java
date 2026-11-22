package com.auth_service.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistroRequest(
        @NotBlank @Size(min = 3, max = 50, message = "El usuario debe tener entre 3 y 50 caracteres") String username,
        @NotBlank @Email(message = "El correo no es válido") String correo,
        @NotBlank @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres") String password) { }