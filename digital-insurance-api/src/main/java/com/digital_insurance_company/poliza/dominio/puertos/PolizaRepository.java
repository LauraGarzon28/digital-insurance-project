package com.digital_insurance_company.poliza.dominio.puertos;

import java.util.Optional;
import java.util.UUID;

import com.digital_insurance_company.poliza.dominio.Poliza;

public interface PolizaRepository {

    Poliza guardar(Poliza poliza);

    Optional<Poliza> buscarPorId(UUID id);
}
