package com.cotizaciones_service.compartido;

public class ClientesNoDisponibleException extends RuntimeException {

    public ClientesNoDisponibleException(Throwable causa) {
        super("No se pudo validar el cliente: el servicio de clientes no está disponible", causa);
    }

}
