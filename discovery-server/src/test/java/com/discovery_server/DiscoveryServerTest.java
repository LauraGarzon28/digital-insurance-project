package com.discovery_server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.netflix.eureka.server.EurekaServerConfigBean;
import org.springframework.core.env.Environment;

/**
 * El servidor de descubrimiento no debe registrarse a sí mismo ni descargar el
 * registro: si lo hiciera, arrancaría en modo cliente y la demo quedaría con un
 * bucle de auto-registro.
 */
@SpringBootTest
class DiscoveryServerTest {

    @Autowired
    private Environment environment;

    @Autowired
    private EurekaServerConfigBean eurekaServerConfig;

    @Test
    void elServidorEurekaSeExponeEnElPuertoEstandar() {
        assertEquals("8761", environment.getProperty("server.port"));
    }

    @Test
    void noSeRegistraASiMismoEnElRegistro() {
        assertFalse(Boolean.parseBoolean(environment.getProperty("eureka.client.register-with-eureka")),
                "El servidor de descubrimiento no debe registrarse como cliente");
        assertFalse(Boolean.parseBoolean(environment.getProperty("eureka.client.fetch-registry")),
                "El servidor de descubrimiento no debe descargar el registro");
    }

    @Test
    void laConfiguracionDelServidorEurekaEstaDisponible() {
        assertNotNull(eurekaServerConfig);
    }
}
