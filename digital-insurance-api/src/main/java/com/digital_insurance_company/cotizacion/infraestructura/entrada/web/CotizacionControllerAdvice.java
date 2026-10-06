package com.digital_insurance_company.cotizacion.infraestructura.entrada.web;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.digital_insurance_company.cotizacion.aplicacion.CotizacionNoEncontradaException;

@RestControllerAdvice
public class CotizacionControllerAdvice {

    @ExceptionHandler(CotizacionNoEncontradaException.class)
    public ResponseEntity<Map<String, String>> manejarNoEncontrada(
            CotizacionNoEncontradaException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("mensaje", exception.getMessage()));
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<Map<String, String>> manejarReglaDeNegocio(
            RuntimeException exception) {
        return ResponseEntity.badRequest()
                .body(Map.of("mensaje", exception.getMessage()));
    }
}
