package com.auditoria_service;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MensajeProcesadoRepository extends JpaRepository<MensajeProcesado, UUID> {
}
