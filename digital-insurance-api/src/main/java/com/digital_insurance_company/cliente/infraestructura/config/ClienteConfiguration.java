package com.digital_insurance_company.cliente.infraestructura.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.digital_insurance_company.cliente.aplicacion.ClienteApplicationService;
import com.digital_insurance_company.cliente.aplicacion.ClienteUseCase;
import com.digital_insurance_company.cliente.aplicacion.RepositorioClientes;
import com.digital_insurance_company.cliente.dominio.ClienteFactory;

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
