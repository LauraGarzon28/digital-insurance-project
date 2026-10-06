package com.digital_insurance_company.siniestros.infraestructura.entrada.web;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.digital_insurance_company.siniestros.dominio.SiniestroNoEncontradoException;

@RestControllerAdvice
public class SiniestroControllerAdvice {

    @ExceptionHandler(SiniestroNoEncontradoException.class)
    public ResponseEntity<Map<String, String>> manejarNoEncontrado(
            SiniestroNoEncontradoException exception) {
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
