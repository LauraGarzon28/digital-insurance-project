package com.clientes_service.cliente.infraestructura.salida.persistencia;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteJpaRepository extends JpaRepository<ClienteJpaEntity, UUID> {

    boolean existsByNumeroDocumento(String numeroDocumento);
    
}