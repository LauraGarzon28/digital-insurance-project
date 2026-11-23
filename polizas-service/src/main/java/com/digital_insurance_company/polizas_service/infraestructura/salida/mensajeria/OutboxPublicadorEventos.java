package com.digital_insurance_company.polizas_service.infraestructura.salida.mensajeria;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.digital_insurance_company.polizas_service.dominio.puertos.PublicadorEventos;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Component
public class OutboxPublicadorEventos implements PublicadorEventos {

    private final OutboxEventRepository repositorio;
    private final ObjectMapper objectMapper;

    public OutboxPublicadorEventos(OutboxEventRepository repositorio, ObjectMapper objectMapper) {
        this.repositorio = repositorio;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publicar(String topico, String clave, Map<String, Object> contenido) {
        try {
            String payload = objectMapper.writeValueAsString(contenido);
            repositorio.save(new OutboxEvent(topico, clave, payload));
        } catch (JacksonException e) {
            throw new IllegalStateException("Error al serializar el evento para el tópico " + topico, e);
        }
    }
}
