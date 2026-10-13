package com.digital_insurance_company.cliente.aplicacion;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import com.digital_insurance_company.cliente.dominio.Cliente;
import com.digital_insurance_company.cliente.dominio.ClienteFactory;

public class ClienteApplicationService implements ClienteUseCase {

    private final ClienteFactory clienteFactory;
    private final RepositorioClientes repositorioClientes;

    public ClienteApplicationService(ClienteFactory clienteFactory,
            RepositorioClientes repositorioClientes) {
        this.clienteFactory = Objects.requireNonNull(clienteFactory);
        this.repositorioClientes = Objects.requireNonNull(repositorioClientes);
    }

    @Override
    public Cliente registrar(String nombreCompleto, String tipoDocumento,
            String numeroDocumento, String correo, String telefono) {
        if (repositorioClientes.existePorNumeroDocumento(numeroDocumento)) {
            throw new ClienteYaRegistradoException(numeroDocumento);
        }

        return repositorioClientes.guardar(clienteFactory.crear(
                nombreCompleto,
                tipoDocumento,
                numeroDocumento,
                correo,
                telefono));
    }

    @Override
    public Optional<Cliente> buscarPorId(UUID id) {
        return repositorioClientes.buscarPorId(id);
    }
}
