package com.auditoria_service;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auditoria/siniestros")
public class AuditoriaController {

    private final RegistroAuditoria registro;

    public AuditoriaController(RegistroAuditoria registro) {
        this.registro = registro;
    }

    @GetMapping("/{siniestroId}")
    public ResponseEntity<List<DecisionResponse>> historial(@PathVariable UUID siniestroId) {
        List<DecisionResponse> historial = registro.historialDe(siniestroId).stream()
                .map(DecisionResponse::desde)
                .toList();

        return historial.isEmpty()
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(historial);
    }
}
