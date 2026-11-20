package com.cotizaciones_service.cotizacion.infraestructura.salida.cliente;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import com.cotizaciones_service.compartido.ClientesNoDisponibleException;
import com.cotizaciones_service.cotizacion.aplicacion.ports.ConsultaClientePort;

@Component
public class ConsultaClienteRestAdapter implements ConsultaClientePort {

    private final RestClient restClient;
    private final CircuitBreaker circuitBreaker;

    public ConsultaClienteRestAdapter(
            @LoadBalanced RestClient.Builder builder,
            CircuitBreakerFactory<?, ?> breakerFactory,
            @Value("${clientes.base-url:http://clientes-service}") String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl).build();
        this.circuitBreaker = breakerFactory.create("clientes");
    }

    @Override
    public boolean existe(UUID clienteId) {
        String token = tokenActual();
        return circuitBreaker.run(() -> consultar(clienteId, token), this::alFallar);
    }

    private boolean consultar(UUID clienteId, String token) {
        try {
            restClient.get()
                    .uri("/clientes/{id}", clienteId)
                    .headers(h -> {
                        if (token != null) {
                            h.setBearerAuth(token);
                        }
                    })
                    .retrieve()
                    .toBodilessEntity();
            return true;
        } catch (HttpClientErrorException.NotFound e) {
            return false; 
        }
    }

    private boolean alFallar(Throwable causa) {
        throw new ClientesNoDisponibleException(causa);
    }

    private String tokenActual() {
        var autenticacion = SecurityContextHolder.getContext().getAuthentication();
        return autenticacion instanceof JwtAuthenticationToken jwt
                ? jwt.getToken().getTokenValue()
                : null;
    }
}