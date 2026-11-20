package com.clientes_service.cliente.aplicacion;

import java.util.Optional;
import java.util.UUID;

import com.clientes_service.cliente.dominio.Cliente;

public interface ClienteUseCase {

    Cliente registrar(String nombreCompleto, String tipoDocumento,
            String numeroDocumento, String correo, String telefono);

    Optional<Cliente> buscarPorId(UUID id);
}
