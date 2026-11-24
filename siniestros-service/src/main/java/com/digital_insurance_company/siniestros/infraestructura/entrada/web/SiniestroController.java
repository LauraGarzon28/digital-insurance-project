package com.digital_insurance_company.siniestros.infraestructura.entrada.web;

import java.net.URI;
import java.util.UUID;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import com.digital_insurance_company.siniestros.dominio.Siniestro;
import com.digital_insurance_company.siniestros.dominio.puertos.SiniestroUseCase;
import com.digital_insurance_company.siniestros.dominio.EstadoSiniestro;

@RestController
@RequestMapping("/api/siniestros")
public class SiniestroController {

    private final SiniestroUseCase siniestroUseCase;

    public SiniestroController(SiniestroUseCase siniestroUseCase) {
        this.siniestroUseCase = siniestroUseCase;
    }

    @PostMapping
    public ResponseEntity<SiniestroResponse> reportar(@RequestBody SiniestroRequest request) {
        Siniestro siniestro = siniestroUseCase.reportarSiniestro(
                request.polizaId(),
                request.descripcion());

        return ResponseEntity
                .created(URI.create("/api/siniestros/" + siniestro.getId()))
                .body(SiniestroResponse.desde(siniestro));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SiniestroResponse> consultar(@PathVariable UUID id) {
        return ResponseEntity.ok(SiniestroResponse.desde(siniestroUseCase.consultarSiniestro(id)));
    }

    @PostMapping("/{id}/evaluacion")
    public ResponseEntity<SiniestroResponse> evaluar(
            @PathVariable UUID id,
            @RequestBody EvaluarSiniestroRequest request) {
        Siniestro siniestro = siniestroUseCase.evaluarSiniestro(
                id,
                request.esFraude(),
                request.montoPropuesto());
        return ResponseEntity.ok(SiniestroResponse.desde(siniestro));
    }

    @PostMapping("/{id}/asignacion")
    public ResponseEntity<SiniestroResponse> asignar(
            @PathVariable UUID id,
            @RequestBody AsignarAjustadorRequest request) {
        Siniestro siniestro = siniestroUseCase.asignarAjustador(id, request.ajustadorId());
        return ResponseEntity.ok(SiniestroResponse.desde(siniestro));
    }

    @GetMapping
    public List<SiniestroResponse> listar(
            @RequestParam(required = false) UUID ajustadorId,
            @RequestParam(required = false) UUID polizaId,
            @RequestParam(required = false) EstadoSiniestro estado) {
        return siniestroUseCase.listarSiniestros(ajustadorId, polizaId, estado).stream()
                .map(SiniestroResponse::desde)
                .toList();
    }
}
