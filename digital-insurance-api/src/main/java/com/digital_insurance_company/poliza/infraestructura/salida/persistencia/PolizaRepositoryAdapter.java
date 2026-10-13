package com.digital_insurance_company.poliza.infraestructura.salida.persistencia;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.digital_insurance_company.poliza.dominio.Poliza;
import com.digital_insurance_company.poliza.dominio.PolizaFactory;
import com.digital_insurance_company.poliza.dominio.puertos.PolizaRepository;

@Component
public class PolizaRepositoryAdapter implements PolizaRepository {

    private final PolizaJpaRepository jpaRepository;
    private final PolizaFactory factory;

    public PolizaRepositoryAdapter(PolizaJpaRepository jpaRepository, PolizaFactory factory) {
        this.jpaRepository = jpaRepository;
        this.factory = factory;
    }

    @Override
    public Poliza guardar(Poliza poliza) {
        PolizaJpaEntity entidad = new PolizaJpaEntity(
                poliza.id(),
                poliza.clienteId(),
                poliza.cotizacionId(),
                poliza.valorAsegurado().monto(),
                poliza.vigencia().fechaInicio(),
                poliza.vigencia().fechaFin(),
                poliza.cancelada());
        jpaRepository.save(entidad);
        return poliza;
    }

    @Override
    public Optional<Poliza> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(e -> factory.reconstruir(
                e.getId(), e.getClienteId(), e.getCotizacionId(), e.getValorAsegurado(),
                e.getFechaInicio(), e.getFechaFin(), e.isCancelada()));
    }
}
