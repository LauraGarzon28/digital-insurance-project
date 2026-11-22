package com.clientes_service.cliente.infraestructura.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.clientes_service.cliente.aplicacion.ClienteApplicationService;
import com.clientes_service.cliente.aplicacion.ClienteUseCase;
import com.clientes_service.cliente.aplicacion.RepositorioClientes;
import com.clientes_service.cliente.dominio.ClienteFactory;

@Configuration
public class ClienteConfiguration {

    @Bean
    ClienteFactory clienteFactory() {
        return new ClienteFactory();
    }

    @Bean
    ClienteUseCase clienteUseCase(
            ClienteFactory clienteFactory,
            RepositorioClientes repositorioClientes) {
        return new ClienteApplicationService(clienteFactory, repositorioClientes);
    }
}
