package com.auth_service.web;

import java.util.Set;
import java.util.UUID;

import com.auth_service.usuario.Rol;

public record UsuarioResponse(UUID id, String username, String correo, Set<Rol> roles) { }