package com.cotizaciones_service.cotizacion.aplicacion.ports;

import java.util.UUID;

public interface ConsultaClientePort {

    boolean existe(UUID clienteId);
}