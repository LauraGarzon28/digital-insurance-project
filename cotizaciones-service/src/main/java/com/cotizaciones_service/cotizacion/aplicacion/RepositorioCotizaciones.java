package com.cotizaciones_service.cotizacion.aplicacion;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

import com.cotizaciones_service.cotizacion.dominio.Cotizacion;

public interface RepositorioCotizaciones {

    Cotizacion guardar(Cotizacion cotizacion);

    Optional<Cotizacion> buscarPorId(UUID id);

    List<Cotizacion> listar(UUID clienteId, String estado);
}
