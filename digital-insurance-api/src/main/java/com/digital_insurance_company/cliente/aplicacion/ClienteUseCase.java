package com.digital_insurance_company.cliente.aplicacion;

import java.util.Optional;
import java.util.UUID;

import com.digital_insurance_company.cliente.dominio.Cliente;

public interface ClienteUseCase {

    Cliente registrar(String nombreCompleto, String tipoDocumento,
            String numeroDocumento, String correo, String telefono);

    Optional<Cliente> buscarPorId(UUID id);
}
