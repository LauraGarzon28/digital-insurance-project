package com.digital_insurance_company.siniestros.infraestructura.entrada.web;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.digital_insurance_company.siniestros.dominio.Siniestro;
import com.digital_insurance_company.siniestros.dominio.puertos.RepositorioSiniestros;

/**
 * Implementación en memoria del puerto, usada solo por las pruebas del adaptador
 * web para no depender de PostgreSQL ni del bróker.
 */
class RepositorioSiniestrosEnMemoria implements RepositorioSiniestros {

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
