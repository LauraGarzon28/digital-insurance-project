package com.auditoria_service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DecisionSiniestroRepository extends JpaRepository<DecisionSiniestro, Long> {

    List<DecisionSiniestro> findBySiniestroIdOrderByRegistradoEnAsc(UUID siniestroId);
}
