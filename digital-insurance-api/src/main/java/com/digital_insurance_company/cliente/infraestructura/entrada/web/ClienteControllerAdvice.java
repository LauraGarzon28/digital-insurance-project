package com.digital_insurance_company.cliente.infraestructura.entrada.web;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.digital_insurance_company.cliente.aplicacion.ClienteYaRegistradoException;

@RestControllerAdvice(assignableTypes = ClienteController.class)
public class ClienteControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> manejarValidacion(
            MethodArgumentNotValidException exception) {
        List<Map<String, String>> errores = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> Map.of(
                        "campo", error.getField(),
                        "mensaje", error.getDefaultMessage()))
                .toList();

        return ResponseEntity.badRequest()
                .body(Map.of(
                        "mensaje", "La solicitud contiene datos inválidos",
                        "errores", errores));
    }

    @ExceptionHandler(ClienteYaRegistradoException.class)
    public ResponseEntity<Map<String, String>> manejarDuplicado(
            ClienteYaRegistradoException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("mensaje", exception.getMessage()));
    }
}
