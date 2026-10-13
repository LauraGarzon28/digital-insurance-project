package com.digital_insurance_company.poliza.infraestructura.entrada.web;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digital_insurance_company.poliza.dominio.Poliza;
import com.digital_insurance_company.poliza.dominio.puertos.PolizaUseCase;

@RestController
@RequestMapping("/api/polizas")
public class PolizaController {

    private final PolizaUseCase casosDeUso;

    public PolizaController(PolizaUseCase casosDeUso) {
        this.casosDeUso = casosDeUso;
    }

    @PostMapping
    public ResponseEntity<PolizaResponse> emitir(@RequestBody EmitirPolizaRequest request) {
        Poliza poliza = casosDeUso.emitirPoliza(
                request.cotizacionId(), request.fechaInicio(), request.fechaFin());
        return ResponseEntity.ok(PolizaResponse.desde(poliza));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PolizaResponse> consultar(@PathVariable UUID id) {
        return ResponseEntity.ok(PolizaResponse.desde(casosDeUso.consultarPoliza(id)));
    }

    @PostMapping("/{id}/renovaciones")
    public ResponseEntity<PolizaResponse> renovar(@PathVariable UUID id) {
        return ResponseEntity.ok(PolizaResponse.desde(casosDeUso.renovarPoliza(id)));
    }

    @PostMapping("/{id}/cancelacion")
    public ResponseEntity<PolizaResponse> cancelar(@PathVariable UUID id) {
        return ResponseEntity.ok(PolizaResponse.desde(casosDeUso.cancelarPoliza(id)));
    }
}
