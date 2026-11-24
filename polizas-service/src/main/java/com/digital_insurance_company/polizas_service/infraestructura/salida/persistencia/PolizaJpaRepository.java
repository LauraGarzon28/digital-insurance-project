package com.digital_insurance_company.polizas_service.infraestructura.salida.persistencia;

import java.util.UUID;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PolizaJpaRepository extends JpaRepository<PolizaJpaEntity, UUID> {

    List<PolizaJpaEntity> findAllByClienteId(UUID clienteId);
}
