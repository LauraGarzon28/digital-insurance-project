package com.digital_insurance_company.polizas_service.dominio.puertos;

import java.util.Map;

public interface PublicadorEventos {

    void publicar(String topico, String clave, Map<String, Object> contenido);
}
