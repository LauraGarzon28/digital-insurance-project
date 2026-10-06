package com.digital_insurance_company.cotizacion.infraestructura.entrada.web;

import java.net.URI;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import com.digital_insurance_company.cotizacion.aplicacion.CotizacionUseCase;
import com.digital_insurance_company.cotizacion.dominio.Cotizacion;
import com.digital_insurance_company.cotizacion.dominio.Riesgo;
import com.digital_insurance_company.cotizacion.dominio.ReglasSuscripcion;

@RestController
@RequestMapping("/api/cotizaciones")
public class CotizacionController {

    private final CotizacionUseCase cotizacionUseCase;

    public CotizacionController(CotizacionUseCase cotizacionUseCase) {
        this.cotizacionUseCase = cotizacionUseCase;
    }

    @PostMapping
    public ResponseEntity<CotizacionResponse> solicitar(
            @Valid @RequestBody CotizacionRequest request) {
        Cotizacion cotizacion = cotizacionUseCase.solicitar(
                request.clienteId(),
                new Riesgo(
                        request.descripcionRiesgo(),
                        request.valorAsegurado(),
                        request.puntajeRiesgo()));

        return ResponseEntity
                .created(URI.create("/api/cotizaciones/" + cotizacion.id()))
                .body(CotizacionResponse.desde(cotizacion));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CotizacionResponse> buscarPorId(@PathVariable UUID id) {
        return cotizacionUseCase.buscarPorId(id)
                .map(cotizacion -> ResponseEntity.ok(CotizacionResponse.desde(cotizacion)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/evaluacion")
    public CotizacionResponse evaluar(
            @PathVariable UUID id,
            @Valid @RequestBody EvaluarCotizacionRequest request) {
        Cotizacion cotizacion = cotizacionUseCase.evaluar(
                id,
                new ReglasSuscripcion(
                        request.versionReglas(),
                        request.tasaBase(),
                        request.umbralRevisionManual()));
        return CotizacionResponse.desde(cotizacion);
    }

    @PostMapping("/{id}/aprobacion")
    public CotizacionResponse aprobarRevisionManual(@PathVariable UUID id) {
        return CotizacionResponse.desde(cotizacionUseCase.aprobarRevisionManual(id));
    }

    @PostMapping("/{id}/rechazo")
    public CotizacionResponse rechazar(@PathVariable UUID id) {
        return CotizacionResponse.desde(cotizacionUseCase.rechazar(id));
    }

    @PostMapping("/{id}/aceptacion")
    public CotizacionResponse aceptar(@PathVariable UUID id) {
        return CotizacionResponse.desde(cotizacionUseCase.aceptar(id));
    }
}
