package com.digital_insurance_company.cliente.infraestructura.entrada.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.digital_insurance_company.cliente.aplicacion.ClienteApplicationService;
import com.digital_insurance_company.cliente.dominio.ClienteFactory;
import com.digital_insurance_company.cliente.infraestructura.salida.persistencia.ClienteRepositoryInMemoryAdapter;

class ClienteControllerTest {

    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new ClienteController(
                    new ClienteApplicationService(
                            new ClienteFactory(),
                            new ClienteRepositoryInMemoryAdapter())))
            .setValidator(new LocalValidatorFactoryBean())
            .setControllerAdvice(new ClienteControllerAdvice())
            .build();

    @Test
    void registraYConsultaUnCliente() throws Exception {
        String response = mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombreCompleto": "Laura Garzón",
                                  "tipoDocumento": "CC",
                                  "numeroDocumento": "1234567890",
                                  "correo": "laura@example.com",
                                  "telefono": "3001234567"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location",
                        org.hamcrest.Matchers.matchesPattern("/clientes/[0-9a-f-]+")))
                .andExpect(jsonPath("$.nombreCompleto").value("Laura Garzón"))
                .andExpect(jsonPath("$.numeroDocumento").value("1234567890"))
                .andReturn()
                .getResponse()
                .getHeader("Location");

        mockMvc.perform(get(response))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correo").value("laura@example.com"));
    }

    @Test
    void rechazaDatosPersonalesInvalidosCon400() throws Exception {
        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombreCompleto": "",
                                  "tipoDocumento": "CC",
                                  "numeroDocumento": "123",
                                  "correo": "correo-invalido",
                                  "telefono": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje")
                        .value("La solicitud contiene datos inválidos"))
                .andExpect(jsonPath("$.errores").isArray());
    }
}
