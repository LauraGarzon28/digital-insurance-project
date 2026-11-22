package com.auth_service.web;

import java.util.Set;

import com.auth_service.usuario.Rol;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record CrearUsuarioRequest(
        @NotBlank @Size(min = 3, max = 50) String username,
        @NotBlank @Email String correo,
        @NotBlank @Size(min = 8) String password,
        @NotEmpty Set<Rol> roles) { }