package com.digital_insurance_company.cotizacion.infraestructura.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.digital_insurance_company.cotizacion.aplicacion.CotizacionApplicationService;
import com.digital_insurance_company.cotizacion.aplicacion.CotizacionUseCase;
import com.digital_insurance_company.cotizacion.aplicacion.RepositorioCotizaciones;
import com.digital_insurance_company.cotizacion.dominio.CotizacionFactory;
import com.digital_insurance_company.cotizacion.dominio.EvaluacionRiesgoService;

@Configuration
public class CotizacionConfiguration {

    @Bean
    @SuppressWarnings("unused")
    CotizacionFactory cotizacionFactory() {
        return new CotizacionFactory();
    }

    @Bean
    @SuppressWarnings("unused")
    EvaluacionRiesgoService evaluacionRiesgoService() {
        return new EvaluacionRiesgoService();
    }

    @Bean
    @SuppressWarnings("unused")
    CotizacionUseCase cotizacionUseCase(
            CotizacionFactory cotizacionFactory,
            EvaluacionRiesgoService evaluacionRiesgoService,
            RepositorioCotizaciones repositorioCotizaciones) {
        return new CotizacionApplicationService(
                cotizacionFactory,
                evaluacionRiesgoService,
                repositorioCotizaciones);
    }
}
