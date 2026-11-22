package com.cotizaciones_service.cotizacion.infraestructura.salida.persistencia;

import java.util.UUID;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CotizacionJpaRepository extends JpaRepository<CotizacionJpaEntity, UUID> {
    List<CotizacionJpaEntity> findByClienteId(UUID clienteId);
    List<CotizacionJpaEntity> findByEstado(String estado);
}