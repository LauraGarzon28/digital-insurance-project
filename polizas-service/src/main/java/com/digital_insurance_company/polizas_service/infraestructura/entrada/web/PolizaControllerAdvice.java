package com.digital_insurance_company.polizas_service.infraestructura.entrada.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.digital_insurance_company.polizas_service.dominio.EmisionNoPermitidaException;
import com.digital_insurance_company.polizas_service.dominio.CotizacionNoEncontradaException;
import com.digital_insurance_company.polizas_service.dominio.PolizaNoEncontradaException;

@RestControllerAdvice
public class PolizaControllerAdvice {

    @ExceptionHandler(EmisionNoPermitidaException.class)
    public ResponseEntity<ErrorResponse> manejarEmisionNoPermitida(EmisionNoPermitidaException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(PolizaNoEncontradaException.class)
    public ResponseEntity<ErrorResponse> manejarPolizaNoEncontrada(PolizaNoEncontradaException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(CotizacionNoEncontradaException.class)
    public ResponseEntity<ErrorResponse> manejarCotizacionNoEncontrada(CotizacionNoEncontradaException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> manejarEstadoInvalido(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> manejarArgumentoInvalido(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ex.getMessage()));
    }
}
