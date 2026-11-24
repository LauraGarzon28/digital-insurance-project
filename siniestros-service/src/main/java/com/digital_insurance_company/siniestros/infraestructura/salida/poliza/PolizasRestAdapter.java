package com.digital_insurance_company.siniestros.infraestructura.salida.poliza;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import com.digital_insurance_company.siniestros.dominio.DatosPolizaParaSiniestro;
import com.digital_insurance_company.siniestros.dominio.EstadoPolizaReferencia;
import com.digital_insurance_company.siniestros.dominio.puertos.ConsultaPolizaPort;

@Component
public class PolizasRestAdapter implements ConsultaPolizaPort {

    private final RestClient restClient;

    public PolizasRestAdapter(
            @LoadBalanced RestClient.Builder builder,
            @Value("${polizas.base-url:http://polizas-service}") String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    @Override
    public Optional<DatosPolizaParaSiniestro> buscarPorId(UUID polizaId) {
        try {
            PolizaResponse poliza = restClient.get()
                    .uri("/api/polizas/{id}", polizaId)
                    .headers(headers -> headers.setBearerAuth(tokenActual()))
                    .retrieve()
                    .body(PolizaResponse.class);

            if (poliza == null) {
                throw new IllegalStateException("Pólizas devolvió una respuesta vacía");
            }

            return Optional.of(new DatosPolizaParaSiniestro(
                    poliza.valorAsegurado(), traducirEstado(poliza.estado())));
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        }
    }

    private EstadoPolizaReferencia traducirEstado(String estado) {
        return switch (estado) {
            case "VIGENTE" -> EstadoPolizaReferencia.ACTIVA;
            case "VENCIDA" -> EstadoPolizaReferencia.VENCIDA;
            case "CANCELADA", "EN_CANCELACION" -> EstadoPolizaReferencia.CANCELADA;
            default -> throw new IllegalStateException("Estado de póliza no soportado: " + estado);
        };
    }

    private String tokenActual() {
        var autenticacion = SecurityContextHolder.getContext().getAuthentication();
        if (autenticacion instanceof JwtAuthenticationToken jwt) {
            return jwt.getToken().getTokenValue();
        }
        throw new IllegalStateException("Se requiere una autenticación JWT para consultar Pólizas");
    }

    private record PolizaResponse(
            UUID id,
            UUID clienteId,
            BigDecimal valorAsegurado,
            LocalDate fechaInicio,
            LocalDate fechaFin,
            String estado) {
    }
}
