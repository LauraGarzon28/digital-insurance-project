package com.digital_insurance_company.cliente.infraestructura.salida.persistencia;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.digital_insurance_company.cliente.aplicacion.RepositorioClientes;
import com.digital_insurance_company.cliente.dominio.Cliente;

@Component
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
