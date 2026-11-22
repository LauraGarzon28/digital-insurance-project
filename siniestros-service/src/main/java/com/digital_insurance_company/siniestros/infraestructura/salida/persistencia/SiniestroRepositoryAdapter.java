package com.digital_insurance_company.siniestros.infraestructura.salida.persistencia;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.digital_insurance_company.siniestros.dominio.Siniestro;
import com.digital_insurance_company.siniestros.dominio.SiniestroFactory;
import com.digital_insurance_company.siniestros.dominio.puertos.RepositorioSiniestros;

@Component
public class SiniestroRepositoryAdapter implements RepositorioSiniestros {

    private final SiniestroJpaRepository repositorioJpa;
    private final SiniestroFactory siniestroFactory;

    public SiniestroRepositoryAdapter(SiniestroJpaRepository repositorioJpa,
            SiniestroFactory siniestroFactory) {
        this.repositorioJpa = repositorioJpa;
        this.siniestroFactory = siniestroFactory;
    }

    @Override
    public Siniestro guardar(Siniestro siniestro) {
        repositorioJpa.save(SiniestroJpaEntity.desde(siniestro));
        return siniestro;
    }

    @Override
    public Optional<Siniestro> buscarPorId(UUID id) {
        return repositorioJpa.findById(id).map(this::aDominio);
    }

    private Siniestro aDominio(SiniestroJpaEntity entidad) {
        return siniestroFactory.reconstituir(
                entidad.getId(),
                entidad.getPolizaId(),
                entidad.getDescripcion(),
                entidad.getEstado(),
                entidad.getValorAseguradoPoliza(),
                entidad.getMontoAprobado());
    }
}
