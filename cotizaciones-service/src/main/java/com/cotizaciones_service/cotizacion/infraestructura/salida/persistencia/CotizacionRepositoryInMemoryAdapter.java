package com.cotizaciones_service.cotizacion.infraestructura.salida.persistencia;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
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

    @Override
    public List<Cotizacion> listar(UUID clienteId, String estado) {
        return new ArrayList<>(cotizaciones.values()).stream()
                .filter(c -> clienteId == null || clienteId.equals(c.clienteId()))
                .filter(c -> estado == null || estado.isBlank() || estado.equals(c.estado().name()))
                .toList();
    }
}
