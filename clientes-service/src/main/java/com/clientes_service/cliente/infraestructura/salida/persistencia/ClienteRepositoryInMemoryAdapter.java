package com.clientes_service.cliente.infraestructura.salida.persistencia;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.clientes_service.cliente.aplicacion.RepositorioClientes;
import com.clientes_service.cliente.dominio.Cliente;

public class ClienteRepositoryInMemoryAdapter implements RepositorioClientes {

    private final Map<UUID, Cliente> clientes = new ConcurrentHashMap<>();

    @Override
    public Cliente guardar(Cliente cliente) {
        clientes.put(cliente.id(), cliente);
        return cliente;
    }

    @Override
    public Optional<Cliente> buscarPorId(UUID id) {
        return Optional.ofNullable(clientes.get(id));
    }

    @Override
    public boolean existePorNumeroDocumento(String numeroDocumento) {
        return clientes.values().stream()
                .anyMatch(cliente -> cliente.numeroDocumento().equals(numeroDocumento));
    }
}
