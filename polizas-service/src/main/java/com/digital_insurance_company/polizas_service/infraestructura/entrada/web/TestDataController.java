package com.digital_insurance_company.polizas_service.infraestructura.entrada.web;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digital_insurance_company.polizas_service.dominio.DatosCotizacionParaEmision;
import com.digital_insurance_company.polizas_service.infraestructura.salida.cotizacion.InMemoryConsultaCotizacionPort;

@RestController
@RequestMapping("/api/test/cotizaciones-aceptadas")
public class TestDataController {

    private final InMemoryConsultaCotizacionPort almacenDePrueba;

    public TestDataController(InMemoryConsultaCotizacionPort almacenDePrueba) {
        this.almacenDePrueba = almacenDePrueba;
    }

    @PostMapping
    public ResponseEntity<CotizacionPruebaResponse> sembrar(
            @RequestBody(required = false) SembrarCotizacionRequest request) {

        UUID cotizacionId = UUID.randomUUID();
        UUID clienteId = (request != null && request.clienteId() != null)
                ? request.clienteId() : UUID.randomUUID();
        BigDecimal valorAsegurado = (request != null && request.valorAsegurado() != null)
                ? request.valorAsegurado() : new BigDecimal("80000000");
        boolean aceptada = request == null || request.aceptada() == null || request.aceptada();

        almacenDePrueba.sembrar(new DatosCotizacionParaEmision(
                cotizacionId, clienteId, valorAsegurado, aceptada));

        return ResponseEntity.ok(new CotizacionPruebaResponse(
                cotizacionId, clienteId, valorAsegurado, aceptada));
    }
}
