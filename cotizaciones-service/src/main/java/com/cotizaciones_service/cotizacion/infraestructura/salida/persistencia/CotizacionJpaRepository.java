package com.cotizaciones_service.cotizacion.infraestructura.salida.persistencia;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CotizacionJpaRepository extends JpaRepository<CotizacionJpaEntity, UUID> {
}