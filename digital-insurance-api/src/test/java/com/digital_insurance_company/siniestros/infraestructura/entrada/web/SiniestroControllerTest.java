package com.digital_insurance_company.siniestros.infraestructura.entrada.web;

import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.digital_insurance_company.siniestros.aplicacion.SiniestroApplicationService;
import com.digital_insurance_company.siniestros.dominio.EvaluacionSiniestroService;
import com.digital_insurance_company.siniestros.dominio.Siniestro;
import com.digital_insurance_company.siniestros.dominio.SiniestroFactory;
import com.digital_insurance_company.siniestros.infraestructura.salida.persistencia.SiniestroRepositoryInMemoryAdapter;

class SiniestroControllerTest {

    private final SiniestroRepositoryInMemoryAdapter repositorio =
            new SiniestroRepositoryInMemoryAdapter();

    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new SiniestroController(
                    new SiniestroApplicationService(
                            new SiniestroFactory(),
                            new EvaluacionSiniestroService(),
                            repositorio)))
            .setControllerAdvice(new SiniestroControllerAdvice())
            .build();

    @Test
    void reportaUnSiniestroYDevuelveElSiniestroCreado() throws Exception {
        mockMvc.perform(post("/api/siniestros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "polizaId": "33333333-3333-3333-3333-333333333333",
                                  "descripcion": "Choque frontal",
                                  "valorAseguradoPoliza": 50000000
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", matchesPattern("/api/siniestros/[0-9a-f-]+")))
                .andExpect(jsonPath("$.estado").value("REPORTADO"))
                .andExpect(jsonPath("$.polizaId").value("33333333-3333-3333-3333-333333333333"));
    }

    @Test
    void consultaUnSiniestroExistente() throws Exception {
        Siniestro siniestro = guardarSiniestro();

        mockMvc.perform(get("/api/siniestros/" + siniestro.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(siniestro.getId().toString()))
                .andExpect(jsonPath("$.estado").value("REPORTADO"));
    }

    @Test
    void evaluaUnSiniestroYLoDejaAprobadoConSuMonto() throws Exception {
        Siniestro siniestro = guardarSiniestro();

        mockMvc.perform(post("/api/siniestros/" + siniestro.getId() + "/evaluacion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "estadoPoliza": "ACTIVA",
                                  "esFraude": false,
                                  "montoPropuesto": 10000
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("APROBADO"))
                .andExpect(jsonPath("$.montoAprobado").value(10000.00));
    }

    @Test
    void devuelve404AlConsultarUnSiniestroInexistente() throws Exception {
        mockMvc.perform(get("/api/siniestros/00000000-0000-0000-0000-000000000000"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value(
                        "No existe el siniestro 00000000-0000-0000-0000-000000000000"));
    }

    @Test
    void rechazaReportarUnSiniestroConValorAseguradoInvalidoCon400() throws Exception {
        mockMvc.perform(post("/api/siniestros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "polizaId": "33333333-3333-3333-3333-333333333333",
                                  "descripcion": "Choque frontal",
                                  "valorAseguradoPoliza": 0
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje")
                        .value("El valor asegurado de la póliza debe ser mayor a cero"));
    }

    @Test
    void rechazaReevaluarUnSiniestroYaResueltoCon400() throws Exception {
        Siniestro siniestro = guardarSiniestro();

        mockMvc.perform(post("/api/siniestros/" + siniestro.getId() + "/evaluacion")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "estadoPoliza": "VENCIDA",
                          "esFraude": false,
                          "montoPropuesto": 1000
                        }
                        """));

        mockMvc.perform(post("/api/siniestros/" + siniestro.getId() + "/evaluacion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "estadoPoliza": "ACTIVA",
                                  "esFraude": false,
                                  "montoPropuesto": 1000
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje")
                        .value("Solo un siniestro reportado puede pasar a evaluación"));
    }

    private Siniestro guardarSiniestro() {
        return repositorio.guardar(new SiniestroFactory().reportarSiniestro(
                UUID.randomUUID(), "Choque frontal", new BigDecimal("50000")));
    }
}
