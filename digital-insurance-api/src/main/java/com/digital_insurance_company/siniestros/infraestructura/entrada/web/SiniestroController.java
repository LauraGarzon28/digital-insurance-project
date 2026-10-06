package com.digital_insurance_company.siniestros.infraestructura.entrada.web;

import java.net.URI;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digital_insurance_company.siniestros.dominio.Siniestro;
import com.digital_insurance_company.siniestros.dominio.puertos.SiniestroUseCase;

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
                request.descripcion(),
                request.valorAseguradoPoliza());

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
                request.estadoPoliza(),
                request.esFraude(),
                request.montoPropuesto());
        return ResponseEntity.ok(SiniestroResponse.desde(siniestro));
    }
}
