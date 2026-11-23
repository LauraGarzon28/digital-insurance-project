package com.digital_insurance_company.polizas_service.infraestructura.salida.mensajeria;

import java.util.Map;
import com.digital_insurance_company.polizas_service.dominio.puertos.PublicadorEventos;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class OutboxPublicadorEventos implements PublicadorEventos{

    private final OutboxEventRepository repositorio;
    private final ObjectMapper objectMapper;

    public OutboxPublicadorEventos(OutboxEventRepository repositorio, ObjectMapper objectMapper) {
        this.repositorio = repositorio;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publicar(String topico, String clave, Map<String, Object> contenido) {
        String payload = objectMapper.writeValueAsString(contenido);
        repositorio.save(new OutboxEvent(topico, clave, payload));
    }
}
