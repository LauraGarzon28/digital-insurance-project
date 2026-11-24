package com.digital_insurance_company.polizas_service.dominio.puertos;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

import com.digital_insurance_company.polizas_service.dominio.Poliza;

public interface PolizaRepository {

    Poliza guardar(Poliza poliza);

    Optional<Poliza> buscarPorId(UUID id);

    List<Poliza> buscarPorClienteId(UUID clienteId);

}
