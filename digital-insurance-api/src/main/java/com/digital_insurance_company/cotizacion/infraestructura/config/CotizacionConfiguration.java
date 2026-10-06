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
    CotizacionFactory cotizacionFactory() {
        return new CotizacionFactory();
    }

    @Bean
    EvaluacionRiesgoService evaluacionRiesgoService() {
        return new EvaluacionRiesgoService();
    }

    @Bean
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
