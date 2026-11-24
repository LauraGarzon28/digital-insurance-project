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
import com.digital_insurance_company.siniestros.dominio.DatosPolizaParaSiniestro;
import com.digital_insurance_company.siniestros.dominio.EstadoPolizaReferencia;
import com.digital_insurance_company.siniestros.dominio.EvaluacionSiniestroService;
import com.digital_insurance_company.siniestros.dominio.Siniestro;
import com.digital_insurance_company.siniestros.dominio.SiniestroFactory;
import com.digital_insurance_company.siniestros.dominio.puertos.ConsultaPolizaPort;
import com.digital_insurance_company.siniestros.dominio.puertos.PublicadorEventosSiniestro;
import java.util.Optional;

class SiniestroControllerTest {

    /** El adaptador web no debe depender del bróker: se prueba con un doble. */
    private static class PublicadorEventosNulo implements PublicadorEventosSiniestro {

        @Override
        public void siniestroReportado(Siniestro siniestro) {
        }

        @Override
        public void siniestroAsignado(Siniestro siniestro) {
        }

        @Override
        public void siniestroResuelto(Siniestro siniestro) {
        }
    }

    private static class ConsultaPolizaFija implements ConsultaPolizaPort {
        private EstadoPolizaReferencia estado = EstadoPolizaReferencia.ACTIVA;
        private boolean encontrada = true;

        @Override
        public Optional<DatosPolizaParaSiniestro> buscarPorId(UUID polizaId) {
            return encontrada
                    ? Optional.of(new DatosPolizaParaSiniestro(new BigDecimal("50000000"), estado))
                    : Optional.empty();
        }
    }

    private final RepositorioSiniestrosEnMemoria repositorio =
            new RepositorioSiniestrosEnMemoria();
    private final ConsultaPolizaFija consultaPoliza = new ConsultaPolizaFija();

    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new SiniestroController(
                    new SiniestroApplicationService(
                            new SiniestroFactory(),
                            new EvaluacionSiniestroService(),
                            repositorio,
                            new PublicadorEventosNulo(),
                            consultaPoliza)))
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
                                  "valorAseguradoPoliza": 1
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", matchesPattern("/api/siniestros/[0-9a-f-]+")))
                .andExpect(jsonPath("$.estado").value("REPORTADO"))
                .andExpect(jsonPath("$.polizaId").value("33333333-3333-3333-3333-333333333333"))
                .andExpect(jsonPath("$.valorAseguradoPoliza").value(50000000));
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
    void asignaUnAjustadorYDejaElSiniestroEnEvaluacion() throws Exception {
        Siniestro siniestro = guardarSiniestro();
        UUID ajustadorId = UUID.randomUUID();

        mockMvc.perform(post("/api/siniestros/" + siniestro.getId() + "/asignacion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "ajustadorId": "%s"
                                }
                                """.formatted(ajustadorId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("EN_EVALUACION"))
                .andExpect(jsonPath("$.ajustadorId").value(ajustadorId.toString()));
    }

    @Test
    void evaluaUnSiniestroYLoDejaAprobadoConSuMonto() throws Exception {
        Siniestro siniestro = guardarSiniestro();

        mockMvc.perform(post("/api/siniestros/" + siniestro.getId() + "/evaluacion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
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
    void usaElValorAseguradoDePolizasAunqueElClienteEnvieOtroValor() throws Exception {
        mockMvc.perform(post("/api/siniestros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "polizaId": "33333333-3333-3333-3333-333333333333",
                                  "descripcion": "Choque frontal",
                                  "valorAseguradoPoliza": 0
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.valorAseguradoPoliza").value(50000000));
    }

    @Test
    void rechazaReevaluarUnSiniestroYaResueltoCon400() throws Exception {
        Siniestro siniestro = guardarSiniestro();

        mockMvc.perform(post("/api/siniestros/" + siniestro.getId() + "/evaluacion")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "esFraude": false,
                          "montoPropuesto": 1000
                        }
                        """));

        mockMvc.perform(post("/api/siniestros/" + siniestro.getId() + "/evaluacion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "esFraude": false,
                                  "montoPropuesto": 1000
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje")
                        .value("Solo un siniestro reportado puede pasar a evaluación"));
    }

    @Test
    void usaElEstadoRealDePolizasEnLugarDelEstadoEnviadoPorElCliente() throws Exception {
        Siniestro siniestro = guardarSiniestro();
        consultaPoliza.estado = EstadoPolizaReferencia.VENCIDA;

        mockMvc.perform(post("/api/siniestros/" + siniestro.getId() + "/evaluacion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "estadoPoliza": "ACTIVA",
                                  "esFraude": false,
                                  "montoPropuesto": 1000
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("RECHAZADO"));
    }

    @Test
    void noReportaUnSiniestroSiLaPolizaNoExiste() throws Exception {
        consultaPoliza.encontrada = false;

        mockMvc.perform(post("/api/siniestros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "polizaId": "33333333-3333-3333-3333-333333333333",
                                  "descripcion": "Choque frontal"
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value(
                        "No existe la póliza 33333333-3333-3333-3333-333333333333"));
    }

    private Siniestro guardarSiniestro() {
        return repositorio.guardar(new SiniestroFactory().reportarSiniestro(
                UUID.randomUUID(), "Choque frontal", new BigDecimal("50000")));
    }
}
