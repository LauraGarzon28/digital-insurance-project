package com.digital_insurance_company.polizas_service.aplicacion;

import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.digital_insurance_company.polizas_service.dominio.DatosCotizacionParaEmision;
import com.digital_insurance_company.polizas_service.dominio.CotizacionNoEncontradaException;
import com.digital_insurance_company.polizas_service.dominio.EmisionNoPermitidaException;
import com.digital_insurance_company.polizas_service.dominio.Poliza;
import com.digital_insurance_company.polizas_service.dominio.PolizaFactory;
import com.digital_insurance_company.polizas_service.dominio.PolizaNoEncontradaException;
import com.digital_insurance_company.polizas_service.dominio.puertos.ConsultaCotizacionPort;
import com.digital_insurance_company.polizas_service.dominio.puertos.PolizaRepository;
import com.digital_insurance_company.polizas_service.dominio.puertos.PublicadorEventos;

class PolizaApplicationServiceTest {
private static final LocalDate INICIO = LocalDate.of(2026, 1, 1);
    private static final LocalDate FIN = LocalDate.of(2026, 12, 31);

    /** Fake del puerto secundario PolizaRepository: un Map en vez de una base de datos. */
    private static class PolizaRepositoryFake implements PolizaRepository {
        final Map<UUID, Poliza> almacen = new HashMap<>();

        @Override
        public Poliza guardar(Poliza poliza) {
            almacen.put(poliza.id(), poliza);
            return poliza;
        }

        @Override
        public Optional<Poliza> buscarPorId(UUID id) {
            return Optional.ofNullable(almacen.get(id));
        }

        @Override
        public List<Poliza> buscarPorClienteId(UUID clienteId) {
            return almacen.values().stream()
                    .filter(poliza -> poliza.clienteId().equals(clienteId))
                    .toList();
        }
    }

    /** Fake del puerto secundario ConsultaCotizacionPort: respuestas fijadas por el test. */
    private static class ConsultaCotizacionFake implements ConsultaCotizacionPort {
        final Map<UUID, DatosCotizacionParaEmision> cotizaciones = new HashMap<>();

        @Override
        public Optional<DatosCotizacionParaEmision> buscarParaEmision(UUID cotizacionId) {
            return Optional.ofNullable(cotizaciones.get(cotizacionId));
        }
    }

    private final PolizaRepositoryFake repositorio = new PolizaRepositoryFake();
    private final ConsultaCotizacionFake consultaCotizacion = new ConsultaCotizacionFake();
        private final PublicadorEventos publicadorEventos = (PublicadorEventos) Proxy.newProxyInstance(
            PublicadorEventos.class.getClassLoader(), new Class<?>[] { PublicadorEventos.class },
            (proxy, method, args) -> null);
    private final PolizaApplicationService casosDeUso =
            new PolizaApplicationService(repositorio, consultaCotizacion, publicadorEventos, new PolizaFactory());

    @Test
    void emitirGuardaLaPolizaEnElRepositorio() {
        UUID cotizacionId = UUID.randomUUID();
        consultaCotizacion.cotizaciones.put(cotizacionId, new DatosCotizacionParaEmision(
                cotizacionId, UUID.randomUUID(), new BigDecimal("80000000"), true));

        Poliza emitida = casosDeUso.emitirPoliza(cotizacionId, INICIO, FIN);

        assertTrue(repositorio.buscarPorId(emitida.id()).isPresent());
    }

    @Test
    void emitirPropagaRechazoDeCotizacionNoAceptada() {
        UUID cotizacionId = UUID.randomUUID();
        consultaCotizacion.cotizaciones.put(cotizacionId, new DatosCotizacionParaEmision(
                cotizacionId, UUID.randomUUID(), new BigDecimal("80000000"), false));

        assertThrows(EmisionNoPermitidaException.class,
                () -> casosDeUso.emitirPoliza(cotizacionId, INICIO, FIN));
    }

    @Test
    void emitirFallaSiLaCotizacionNoExiste() {
        assertThrows(CotizacionNoEncontradaException.class,
                () -> casosDeUso.emitirPoliza(UUID.randomUUID(), INICIO, FIN));
    }

    @Test
    void renovarBuscaModificaYGuarda() {
        Poliza existente = new PolizaFactory().nueva(
                UUID.randomUUID(), UUID.randomUUID(), new BigDecimal("1000"), INICIO, FIN);
        repositorio.guardar(existente);

        Poliza renovada = casosDeUso.renovarPoliza(existente.id());

        assertEquals(FIN.plusDays(1), renovada.vigencia().fechaInicio());
    }

    @Test
    void consultarUnaPolizaInexistenteLanzaExcepcionDeDominio() {
        assertThrows(PolizaNoEncontradaException.class,
                () -> casosDeUso.consultarPoliza(UUID.randomUUID()));
    }

    @Test
    void listarPorClienteDevuelveSoloSusPolizas() {
        UUID clienteId = UUID.randomUUID();
        UUID otraClienteId = UUID.randomUUID();
        repositorio.guardar(new PolizaFactory().nueva(
                clienteId, UUID.randomUUID(), new BigDecimal("1000"), INICIO, FIN));
        repositorio.guardar(new PolizaFactory().nueva(
                otraClienteId, UUID.randomUUID(), new BigDecimal("2000"), INICIO, FIN));

        assertEquals(1, casosDeUso.listarPorCliente(clienteId).size());
        assertTrue(casosDeUso.listarPorCliente(clienteId).stream()
                .allMatch(poliza -> poliza.clienteId().equals(clienteId)));
    }
}
