package com.digital_insurance_company.polizas_service.infraestructura.salida.cotizacion;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import com.digital_insurance_company.polizas_service.dominio.DatosCotizacionParaEmision;
import com.digital_insurance_company.polizas_service.dominio.puertos.ConsultaCotizacionPort;

@Component
public class CotizacionesRestAdapter implements ConsultaCotizacionPort {

    private final RestClient restClient;

    public CotizacionesRestAdapter(
            @LoadBalanced
            RestClient.Builder builder,
            @Value("${cotizaciones.base-url:http://cotizaciones-service}") String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    @Override
    public Optional<DatosCotizacionParaEmision> buscarParaEmision(UUID cotizacionId) {
        try {
            CotizacionResponse cotizacion = restClient.get()
                    .uri("/cotizaciones/{id}", cotizacionId)
                    .headers(headers -> headers.setBearerAuth(tokenActual()))
                    .retrieve()
                    .body(CotizacionResponse.class);

            if (cotizacion == null) {
                throw new IllegalStateException("Cotizaciones devolvió una respuesta vacía");
            }

            return Optional.of(new DatosCotizacionParaEmision(
                    cotizacion.id(),
                    cotizacion.clienteId(),
                    cotizacion.valorAsegurado(),
                    "ACEPTADA".equals(cotizacion.estado())));
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        }
    }

    private String tokenActual() {
        var autenticacion = SecurityContextHolder.getContext().getAuthentication();
        if (autenticacion instanceof JwtAuthenticationToken jwt) {
            return jwt.getToken().getTokenValue();
        }
        throw new IllegalStateException("Se requiere una autenticación JWT para consultar Cotizaciones");
    }

    private record CotizacionResponse(
            UUID id,
            UUID clienteId,
            String descripcionRiesgo,
            BigDecimal valorAsegurado,
            int puntajeRiesgo,
            String estado,
            PrimaResponse prima) {
    }

    private record PrimaResponse(BigDecimal monto, String versionReglas) {
    }
}
