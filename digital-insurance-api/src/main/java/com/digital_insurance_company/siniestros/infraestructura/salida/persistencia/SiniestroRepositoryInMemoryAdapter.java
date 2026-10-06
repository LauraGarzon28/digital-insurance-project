package com.digital_insurance_company.siniestros.infraestructura.salida.persistencia;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.digital_insurance_company.siniestros.dominio.Siniestro;
import com.digital_insurance_company.siniestros.dominio.puertos.RepositorioSiniestros;

@Component
public class SiniestroRepositoryInMemoryAdapter implements RepositorioSiniestros {

    private final Map<UUID, Siniestro> almacen = new ConcurrentHashMap<>();

    @Override
    public Siniestro guardar(Siniestro siniestro) {
        almacen.put(siniestro.getId(), siniestro);
        return siniestro;
    }

    @Override
    public Optional<Siniestro> buscarPorId(UUID id) {
        return Optional.ofNullable(almacen.get(id));
    }
}
