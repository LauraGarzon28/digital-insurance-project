package com.digital_insurance_company.cotizacion.infraestructura.entrada.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.digital_insurance_company.cotizacion.aplicacion.CotizacionApplicationService;
import com.digital_insurance_company.cotizacion.dominio.CotizacionFactory;
import com.digital_insurance_company.cotizacion.dominio.EvaluacionRiesgoService;

class CotizacionControllerTest {

    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new CotizacionController(
                    new CotizacionApplicationService(
                            new CotizacionFactory(),
                            new EvaluacionRiesgoService())))
            .build();

    @Test
    void solicitaUnaCotizacionYDevuelveLaCotizacionCreada() throws Exception {
        String request = """
                {
                  "clienteId": "2f5e2d71-4ab1-4d08-8d3f-0b8329f8d3c8",
                  "descripcionRiesgo": "Vivienda",
                  "valorAsegurado": 100000000,
                  "puntajeRiesgo": 30
                }
                """;

        mockMvc.perform(post("/api/cotizaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location",
                        org.hamcrest.Matchers.matchesPattern(
                                "/api/cotizaciones/[0-9a-f-]+")))
                .andExpect(jsonPath("$.clienteId")
                        .value("2f5e2d71-4ab1-4d08-8d3f-0b8329f8d3c8"))
                .andExpect(jsonPath("$.estado").value("SOLICITADA"))
                .andExpect(jsonPath("$.prima").doesNotExist());
    }
}
