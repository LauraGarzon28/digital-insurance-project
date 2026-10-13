package com.digital_insurance_company.siniestros.infraestructura.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.digital_insurance_company.siniestros.aplicacion.SiniestroApplicationService;
import com.digital_insurance_company.siniestros.dominio.EvaluacionSiniestroService;
import com.digital_insurance_company.siniestros.dominio.SiniestroFactory;
import com.digital_insurance_company.siniestros.dominio.puertos.RepositorioSiniestros;
import com.digital_insurance_company.siniestros.dominio.puertos.SiniestroUseCase;

@Configuration
public class SiniestroConfiguration {

    @Bean
    SiniestroFactory siniestroFactory() {
        return new SiniestroFactory();
    }

    @Bean
    EvaluacionSiniestroService evaluacionSiniestroService() {
        return new EvaluacionSiniestroService();
    }

    @Bean
    SiniestroUseCase siniestroUseCase(
            SiniestroFactory siniestroFactory,
            EvaluacionSiniestroService evaluacionSiniestroService,
            RepositorioSiniestros repositorioSiniestros) {
        return new SiniestroApplicationService(
                siniestroFactory,
                evaluacionSiniestroService,
                repositorioSiniestros);
    }
}
