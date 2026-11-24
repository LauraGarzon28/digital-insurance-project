package com.cotizaciones_service.cotizacion.infraestructura.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

import com.cotizaciones_service.cotizacion.aplicacion.CotizacionApplicationService;
import com.cotizaciones_service.cotizacion.aplicacion.CotizacionUseCase;
import com.cotizaciones_service.cotizacion.aplicacion.RepositorioCotizaciones;
import com.cotizaciones_service.cotizacion.aplicacion.ports.ConsultaClientePort;
import com.cotizaciones_service.cotizacion.dominio.CotizacionFactory;
import com.cotizaciones_service.cotizacion.dominio.EvaluacionRiesgoService;
import com.cotizaciones_service.cotizacion.dominio.ReglasSuscripcion;
import java.math.BigDecimal;

@Configuration
public class CotizacionConfiguration {

    @Bean
    CotizacionFactory cotizacionFactory() {
        return new CotizacionFactory();
    }

    @Bean
    EvaluacionRiesgoService evaluacionRiesgoService() {
        return new EvaluacionRiesgoService();
    }

    @Bean
    ReglasSuscripcion reglasSuscripcion(
            @Value("${suscripcion.version:2026.2}") String version,
            @Value("${suscripcion.tasa-base:0.035}") BigDecimal tasaBase,
            @Value("${suscripcion.umbral-revision-manual:70}") int umbralRevisionManual) {
        return new ReglasSuscripcion(version, tasaBase, umbralRevisionManual);
    }

    @Bean
    CotizacionUseCase cotizacionUseCase(
            CotizacionFactory cotizacionFactory,
            EvaluacionRiesgoService evaluacionRiesgoService,
            RepositorioCotizaciones repositorioCotizaciones,
            ConsultaClientePort consultaCliente,
            ReglasSuscripcion reglasSuscripcion) {
        return new CotizacionApplicationService(
                cotizacionFactory,
                evaluacionRiesgoService,
                repositorioCotizaciones,
                consultaCliente,
                reglasSuscripcion);
    }
}
