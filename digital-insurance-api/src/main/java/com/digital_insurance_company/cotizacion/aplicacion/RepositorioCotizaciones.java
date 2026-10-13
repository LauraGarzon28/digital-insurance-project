package com.digital_insurance_company.cotizacion.aplicacion;

import java.util.Optional;
import java.util.UUID;

import com.digital_insurance_company.cotizacion.dominio.Cotizacion;

public interface RepositorioCotizaciones {

    Cotizacion guardar(Cotizacion cotizacion);

    Optional<Cotizacion> buscarPorId(UUID id);
}
