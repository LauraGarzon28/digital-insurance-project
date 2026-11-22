package com.cotizaciones_service.cotizacion.infraestructura.salida.persistencia;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.cotizaciones_service.cotizacion.aplicacion.RepositorioCotizaciones;
import com.cotizaciones_service.cotizacion.dominio.Cotizacion;

public class CotizacionRepositoryInMemoryAdapter implements RepositorioCotizaciones {

    private final Map<UUID, Cotizacion> cotizaciones = new ConcurrentHashMap<>();

    @Override
    public Cotizacion guardar(Cotizacion cotizacion) {
        cotizaciones.put(cotizacion.id(), cotizacion);
        return cotizacion;
    }

    @Override
    public Optional<Cotizacion> buscarPorId(UUID id) {
        return Optional.ofNullable(cotizaciones.get(id));
    }
}
