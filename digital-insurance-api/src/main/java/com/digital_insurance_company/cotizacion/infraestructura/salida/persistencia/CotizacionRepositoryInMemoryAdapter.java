package com.digital_insurance_company.cotizacion.infraestructura.salida.persistencia;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.digital_insurance_company.cotizacion.aplicacion.RepositorioCotizaciones;
import com.digital_insurance_company.cotizacion.dominio.Cotizacion;

@Component
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
