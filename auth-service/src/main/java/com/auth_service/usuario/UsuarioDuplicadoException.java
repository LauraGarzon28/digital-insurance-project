package com.auth_service.usuario;

public class UsuarioDuplicadoException extends RuntimeException {
    public UsuarioDuplicadoException(String detalle) {
        super("Ya existe un usuario con ese " + detalle);
    }
}