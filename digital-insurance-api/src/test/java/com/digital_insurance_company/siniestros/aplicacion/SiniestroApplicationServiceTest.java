package com.digital_insurance_company.siniestros.aplicacion;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.digital_insurance_company.siniestros.dominio.EstadoPolizaReferencia;
import com.digital_insurance_company.siniestros.dominio.EstadoSiniestro;
import com.digital_insurance_company.siniestros.dominio.EvaluacionSiniestroService;
import com.digital_insurance_company.siniestros.dominio.Siniestro;
import com.digital_insurance_company.siniestros.dominio.SiniestroFactory;
import com.digital_insurance_company.siniestros.dominio.SiniestroNoEncontradoException;
import com.digital_insurance_company.siniestros.dominio.puertos.RepositorioSiniestros;
import com.digital_insurance_company.siniestros.dominio.puertos.SiniestroUseCase;

class SiniestroApplicationServiceTest {

    private static final UUID POLIZA_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");

    /**
     * Fake del puerto secundario RepositorioSiniestros: un Map en vez de una base
     * de datos.
     */
    private static class RepositorioSiniestrosFake implements RepositorioSiniestros {

        final Map<UUID, Siniestro> almacen = new HashMap<>();

        @Override
        public Siniestro guardar(Siniestro siniestro) {
            almacen.put(siniestro.getId(), siniestro);
            return siniestro;
        }

        @Override
        public Optional<Siniestro> buscarPorId(UUID id) {
            return Optional.ofNullable(almacen.get(id));
        }
    }

    private final RepositorioSiniestrosFake repositorio = new RepositorioSiniestrosFake();
    private final SiniestroUseCase casosDeUso = new SiniestroApplicationService(
            new SiniestroFactory(), new EvaluacionSiniestroService(), repositorio);

    @Test
    void reportarGuardaElSiniestroEnEstadoReportado() {
        Siniestro siniestro = casosDeUso.reportarSiniestro(POLIZA_ID, "Choque frontal", new BigDecimal("50000"));

        assertEquals(EstadoSiniestro.REPORTADO, siniestro.getEstado());
        assertTrue(repositorio.buscarPorId(siniestro.getId()).isPresent());
    }

    @Test
    void evaluarConPolizaVencidaRechazaElSiniestro() {
        Siniestro siniestro = casosDeUso.reportarSiniestro(POLIZA_ID, "Daños por agua", new BigDecimal("20000"));

        Siniestro evaluado = casosDeUso.evaluarSiniestro(
                siniestro.getId(), EstadoPolizaReferencia.VENCIDA, false, new BigDecimal("5000"));

        assertEquals(EstadoSiniestro.RECHAZADO, evaluado.getEstado());
    }

    @Test
    void evaluarSinFraudeConPolizaActivaApruebaElSiniestro() {
        Siniestro siniestro = casosDeUso.reportarSiniestro(POLIZA_ID, "Robo de equipo", new BigDecimal("15000"));

        Siniestro evaluado = casosDeUso.evaluarSiniestro(
                siniestro.getId(), EstadoPolizaReferencia.ACTIVA, false, new BigDecimal("10000"));

        assertEquals(EstadoSiniestro.APROBADO, evaluado.getEstado());
        assertNotNull(evaluado.getMontoAprobado());
    }

    @Test
    void evaluarUnSiniestroInexistenteLanzaExcepcionDeDominio() {
        SiniestroNoEncontradoException excepcion = assertThrows(SiniestroNoEncontradoException.class,
                () -> casosDeUso.evaluarSiniestro(UUID.randomUUID(),
                        EstadoPolizaReferencia.ACTIVA, false, new BigDecimal("1000")));
        assertNotNull(excepcion);
    }

    @Test
    void consultarUnSiniestroInexistenteLanzaExcepcionDeDominio() {
        assertThrows(SiniestroNoEncontradoException.class,
                () -> casosDeUso.consultarSiniestro(UUID.randomUUID()));
    }
}
