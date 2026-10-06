package com.digital_insurance_company.poliza.infraestructura.salida.persistencia;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
    
public interface PolizaJpaRepository extends JpaRepository<PolizaJpaEntity, UUID> {

}
