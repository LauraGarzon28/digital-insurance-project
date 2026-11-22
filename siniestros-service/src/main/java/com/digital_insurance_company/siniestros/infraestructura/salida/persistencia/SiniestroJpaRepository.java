package com.digital_insurance_company.siniestros.infraestructura.salida.persistencia;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SiniestroJpaRepository extends JpaRepository<SiniestroJpaEntity, UUID> {
}
