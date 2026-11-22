package com.clientes_service.cliente.aplicacion;

import java.util.Optional;
import java.util.UUID;

import com.clientes_service.cliente.dominio.Cliente;

public interface RepositorioClientes {

    Cliente guardar(Cliente cliente);

    Optional<Cliente> buscarPorId(UUID id);

    boolean existePorNumeroDocumento(String numeroDocumento);
}
