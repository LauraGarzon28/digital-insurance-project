package com.digital_insurance_company.cliente.infraestructura.entrada.web;

import java.net.URI;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import com.digital_insurance_company.cliente.aplicacion.ClienteUseCase;
import com.digital_insurance_company.cliente.dominio.Cliente;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteUseCase clienteUseCase;

    public ClienteController(ClienteUseCase clienteUseCase) {
        this.clienteUseCase = clienteUseCase;
    }

    @PostMapping
    public ResponseEntity<ClienteResponse> registrar(
            @Valid @RequestBody ClienteRequest request) {
        Cliente cliente = clienteUseCase.registrar(
                request.nombreCompleto(),
                request.tipoDocumento(),
                request.numeroDocumento(),
                request.correo(),
                request.telefono());

        return ResponseEntity
                .created(URI.create("/clientes/" + cliente.id()))
                .body(ClienteResponse.desde(cliente));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> buscarPorId(@PathVariable UUID id) {
        return clienteUseCase.buscarPorId(id)
                .map(cliente -> ResponseEntity.ok(ClienteResponse.desde(cliente)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
