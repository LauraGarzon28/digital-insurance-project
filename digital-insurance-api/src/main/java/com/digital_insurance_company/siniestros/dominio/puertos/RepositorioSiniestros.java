package com.digital_insurance_company.siniestros.dominio.puertos;

import java.util.Optional;
import java.util.UUID;

import com.digital_insurance_company.siniestros.dominio.Siniestro;

public interface RepositorioSiniestros {

    Siniestro guardar(Siniestro siniestro);

    Optional<Siniestro> buscarPorId(UUID id);
}
