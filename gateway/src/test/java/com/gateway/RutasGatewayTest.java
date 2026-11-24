package com.gateway;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.core.env.Environment;

/**
 * Verifica la configuración de enrutamiento declarada. Es la prueba que protege el
 * contrato del gateway: si una ruta desaparece o un servicio cambia de nombre lógico,
 * la demo deja de funcionar y esta prueba lo delata.
 */
@SpringBootTest
class RutasGatewayTest {

    @Autowired
    private RouteLocator routeLocator;

    @Test
    void declaraUnaRutaPorCadaServicioDelSistema() {
        List<String> ids = routeLocator.getRoutes().map(Route::getId).collectList().block();

        assertNotNull(ids);
        assertTrue(ids.containsAll(List.of("auth", "clientes", "cotizaciones", "siniestros", "auditoria", "polizas")),
                "Faltan rutas de servicios en " + ids);
    }

    @Test
    void lasRutasDeServiciosUsanBalanceoPorEureka() {
        List<String> uris = routeLocator.getRoutes()
                .map(route -> route.getUri().toString())
                .collectList()
                .block();

        assertNotNull(uris);
        assertTrue(uris.stream().allMatch(uri -> uri.startsWith("lb://")),
                "Las rutas deben resolverse por Eureka, pero fueron " + uris);
    }

    @Test
    void polizasSeEnrutaAlServicioDescubiertoPorEureka() {
        String uriPolizas = routeLocator.getRoutes()
                .filter(route -> "polizas".equals(route.getId()))
                .map(route -> route.getUri().toString())
                .blockFirst();

        assertEquals("lb://polizas-service", uriPolizas);
    }

    @Test
    void siniestrosYaNoSeEnrutaAlMonolito() {
        String uriSiniestros = routeLocator.getRoutes()
                .filter(route -> "siniestros".equals(route.getId()))
                .map(route -> route.getUri().toString())
                .blockFirst();

        assertEquals("lb://siniestros-service", uriSiniestros);
    }
}
