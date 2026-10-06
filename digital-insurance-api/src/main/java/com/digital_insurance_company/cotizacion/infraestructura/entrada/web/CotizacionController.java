package com.digital_insurance_company.cotizacion.infraestructura.entrada.web;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digital_insurance_company.cotizacion.aplicacion.CotizacionUseCase;
import com.digital_insurance_company.cotizacion.dominio.Cotizacion;
import com.digital_insurance_company.cotizacion.dominio.Riesgo;

@RestController
@RequestMapping("/api/cotizaciones")
public class CotizacionController {

    private final CotizacionUseCase cotizacionUseCase;

    public CotizacionController(CotizacionUseCase cotizacionUseCase) {
        this.cotizacionUseCase = cotizacionUseCase;
    }

    @PostMapping
    public ResponseEntity<CotizacionResponse> solicitar(@RequestBody CotizacionRequest request) {
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
}
