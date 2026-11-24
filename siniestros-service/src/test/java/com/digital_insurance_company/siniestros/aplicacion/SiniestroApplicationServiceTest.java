package com.digital_insurance_company.siniestros.aplicacion;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import com.digital_insurance_company.siniestros.dominio.DatosPolizaParaSiniestro;
import com.digital_insurance_company.siniestros.dominio.PolizaNoEncontradaException;
import com.digital_insurance_company.siniestros.dominio.Siniestro;
import com.digital_insurance_company.siniestros.dominio.SiniestroFactory;
import com.digital_insurance_company.siniestros.dominio.SiniestroNoEncontradoException;
import com.digital_insurance_company.siniestros.dominio.puertos.ConsultaPolizaPort;
import com.digital_insurance_company.siniestros.dominio.puertos.PublicadorEventosSiniestro;
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

        @Override
        public java.util.List<Siniestro> listarTodos() {
            return new java.util.ArrayList<>(almacen.values());
        }
    }

    /**
     * Fake del publicador de eventos: registra los hechos en una lista para poder
     * afirmar sobre ellos sin levantar un bróker.
     */
    private static class PublicadorEventosFake implements PublicadorEventosSiniestro {

        final List<EstadoSiniestro> reportados = new ArrayList<>();
        final List<UUID> asignados = new ArrayList<>();
        final List<EstadoSiniestro> resueltos = new ArrayList<>();

        @Override
        public void siniestroReportado(Siniestro siniestro) {
            reportados.add(siniestro.getEstado());
        }

        @Override
        public void siniestroAsignado(Siniestro siniestro) {
            asignados.add(siniestro.getAjustadorId());
        }

        @Override
        public void siniestroResuelto(Siniestro siniestro) {
            resueltos.add(siniestro.getEstado());
        }
    }

    private static class ConsultaPolizaFake implements ConsultaPolizaPort {
        final Map<UUID, DatosPolizaParaSiniestro> polizas = new HashMap<>();

        @Override
        public Optional<DatosPolizaParaSiniestro> buscarPorId(UUID polizaId) {
            return Optional.ofNullable(polizas.get(polizaId));
        }
    }

    private final RepositorioSiniestrosFake repositorio = new RepositorioSiniestrosFake();
    private final PublicadorEventosFake publicador = new PublicadorEventosFake();
    private final ConsultaPolizaFake consultaPoliza = new ConsultaPolizaFake();
    private final SiniestroUseCase casosDeUso = new SiniestroApplicationService(
            new SiniestroFactory(), new EvaluacionSiniestroService(), repositorio, publicador, consultaPoliza);

    private Siniestro reportar(String descripcion, BigDecimal valorAsegurado) {
        consultaPoliza.polizas.put(POLIZA_ID, new DatosPolizaParaSiniestro(
                valorAsegurado, EstadoPolizaReferencia.ACTIVA));
        return casosDeUso.reportarSiniestro(POLIZA_ID, descripcion);
    }

    @Test
    void reportarGuardaElSiniestroConElValorAseguradoConsultado() {
        Siniestro siniestro = reportar("Choque frontal", new BigDecimal("50000"));

        assertEquals(EstadoSiniestro.REPORTADO, siniestro.getEstado());
        assertEquals(new BigDecimal("50000"), siniestro.getValorAseguradoPoliza());
        assertTrue(repositorio.buscarPorId(siniestro.getId()).isPresent());
    }

    @Test
    void reportarPublicaElEventoSiniestroReportado() {
        reportar("Choque frontal", new BigDecimal("50000"));

        assertEquals(List.of(EstadoSiniestro.REPORTADO), publicador.reportados);
        assertTrue(publicador.resueltos.isEmpty());
    }

    @Test
    void asignarAjustadorGuardaLaAsignacionYPasaAEnEvaluacion() {
        Siniestro siniestro = reportar("Choque frontal", new BigDecimal("50000"));
        UUID ajustadorId = UUID.randomUUID();

        Siniestro asignado = casosDeUso.asignarAjustador(siniestro.getId(), ajustadorId);

        assertEquals(ajustadorId, asignado.getAjustadorId());
        assertEquals(EstadoSiniestro.EN_EVALUACION, asignado.getEstado());
        assertEquals(List.of(ajustadorId), publicador.asignados);
    }

    @Test
    void evaluarConPolizaVencidaRechazaElSiniestro() {
        Siniestro siniestro = reportar("Daños por agua", new BigDecimal("20000"));
        consultaPoliza.polizas.put(POLIZA_ID, new DatosPolizaParaSiniestro(
                new BigDecimal("20000"), EstadoPolizaReferencia.VENCIDA));

        Siniestro evaluado = casosDeUso.evaluarSiniestro(
                siniestro.getId(), false, new BigDecimal("5000"));

        assertEquals(EstadoSiniestro.RECHAZADO, evaluado.getEstado());
    }

    @Test
    void evaluarSinFraudeConPolizaActivaApruebaElSiniestro() {
        Siniestro siniestro = reportar("Robo de equipo", new BigDecimal("15000"));

        Siniestro evaluado = casosDeUso.evaluarSiniestro(
                siniestro.getId(), false, new BigDecimal("10000"));

        assertEquals(EstadoSiniestro.APROBADO, evaluado.getEstado());
        assertNotNull(evaluado.getMontoAprobado());
    }

    @Test
    void evaluarPublicaElEventoSiniestroResueltoConElEstadoFinal() {
        Siniestro siniestro = reportar("Robo de equipo", new BigDecimal("15000"));

        casosDeUso.evaluarSiniestro(
                siniestro.getId(), false, new BigDecimal("10000"));

        assertEquals(List.of(EstadoSiniestro.APROBADO), publicador.resueltos);
    }

    @Test
    void unRechazoTambienPublicaElEventoSiniestroResuelto() {
        Siniestro siniestro = reportar("Daños por agua", new BigDecimal("20000"));

        casosDeUso.evaluarSiniestro(
                siniestro.getId(), true, new BigDecimal("5000"));

        assertEquals(List.of(EstadoSiniestro.RECHAZADO), publicador.resueltos);
    }

    @Test
    void unaEvaluacionFallidaNoPublicaEventos() {
        Siniestro siniestro = reportar("Daños por agua", new BigDecimal("20000"));

        assertThrows(IllegalArgumentException.class, () -> casosDeUso.evaluarSiniestro(
                siniestro.getId(), false, new BigDecimal("999999")));

        assertTrue(publicador.resueltos.isEmpty());
    }

    @Test
    void evaluarUnSiniestroInexistenteLanzaExcepcionDeDominio() {
        SiniestroNoEncontradoException excepcion = assertThrows(SiniestroNoEncontradoException.class,
                () -> casosDeUso.evaluarSiniestro(UUID.randomUUID(), false, new BigDecimal("1000")));
        assertNotNull(excepcion);
    }

    @Test
    void reportarConPolizaInexistenteLanzaExcepcionDeDominio() {
        assertThrows(PolizaNoEncontradaException.class,
                () -> casosDeUso.reportarSiniestro(POLIZA_ID, "Choque frontal"));
    }

    @Test
    void consultarUnSiniestroInexistenteLanzaExcepcionDeDominio() {
        assertThrows(SiniestroNoEncontradoException.class,
                () -> casosDeUso.consultarSiniestro(UUID.randomUUID()));
    }
}
