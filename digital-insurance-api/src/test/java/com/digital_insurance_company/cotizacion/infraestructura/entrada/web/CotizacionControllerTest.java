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
import com.digital_insurance_company.cotizacion.dominio.Cotizacion;
import com.digital_insurance_company.cotizacion.infraestructura.salida.persistencia.CotizacionRepositoryInMemoryAdapter;
import com.digital_insurance_company.cotizacion.dominio.CotizacionFactory;
import com.digital_insurance_company.cotizacion.dominio.EvaluacionRiesgoService;

class CotizacionControllerTest {

    private final CotizacionRepositoryInMemoryAdapter repositorio =
            new CotizacionRepositoryInMemoryAdapter();

    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new CotizacionController(
                    new CotizacionApplicationService(
                            new CotizacionFactory(),
                            new EvaluacionRiesgoService(),
                            repositorio)))
            .setControllerAdvice(new CotizacionControllerAdvice())
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

    @Test
    void consultaUnaCotizacionExistente() throws Exception {
        Cotizacion cotizacion = guardarCotizacion();

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .get("/api/cotizaciones/" + cotizacion.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cotizacion.id().toString()))
                .andExpect(jsonPath("$.estado").value("SOLICITADA"));
    }

    @Test
    void evaluaUnaCotizacionYCalculaSuPrima() throws Exception {
        Cotizacion cotizacion = guardarCotizacion();

        mockMvc.perform(post("/api/cotizaciones/" + cotizacion.id() + "/evaluacion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "versionReglas": "v1",
                                  "tasaBase": 0.02,
                                  "umbralRevisionManual": 70
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("COTIZADA"))
                .andExpect(jsonPath("$.prima.monto").value(2600000.00))
                .andExpect(jsonPath("$.prima.versionReglas").value("v1"));
    }

    @Test
    void devuelve404AlConsultarUnaCotizacionInexistente() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .get("/api/cotizaciones/00000000-0000-0000-0000-000000000000"))
                .andExpect(status().isNotFound());
    }

    private Cotizacion guardarCotizacion() {
        return repositorio.guardar(new CotizacionFactory().crear(
                java.util.UUID.randomUUID(),
                "Vivienda",
                new java.math.BigDecimal("100000000"),
                30));
    }
}
