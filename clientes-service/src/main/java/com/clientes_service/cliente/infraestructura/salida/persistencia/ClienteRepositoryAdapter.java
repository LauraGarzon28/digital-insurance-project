package com.clientes_service.cliente.infraestructura.salida.persistencia;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.clientes_service.cliente.aplicacion.RepositorioClientes;
import com.clientes_service.cliente.dominio.Cliente;

@Component
public class ClienteRepositoryAdapter implements RepositorioClientes {

    private final ClienteJpaRepository jpa;

    public ClienteRepositoryAdapter(ClienteJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Cliente guardar(Cliente c) {
        jpa.save(new ClienteJpaEntity(c.id(), c.nombreCompleto(), c.tipoDocumento(),
                c.numeroDocumento(), c.correo(), c.telefono()));
        return c;
    }

    @Override
    public Optional<Cliente> buscarPorId(UUID id) {
        return jpa.findById(id).map(this::aDominio);
    }

    @Override
    public boolean existePorNumeroDocumento(String numeroDocumento) {
        return jpa.existsByNumeroDocumento(numeroDocumento);
    }

    private Cliente aDominio(ClienteJpaEntity e) {
        return new Cliente(e.getId(), e.getNombreCompleto(), e.getTipoDocumento(),
                e.getNumeroDocumento(), e.getCorreo(), e.getTelefono());
    }
}