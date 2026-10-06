package com.digital_insurance_company.poliza.aplicacion;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.digital_insurance_company.poliza.dominio.DatosCotizacionParaEmision;
import com.digital_insurance_company.poliza.dominio.EmisionNoPermitidaException;
import com.digital_insurance_company.poliza.dominio.Poliza;
import com.digital_insurance_company.poliza.dominio.PolizaFactory;
import com.digital_insurance_company.poliza.dominio.PolizaNoEncontradaException;
import com.digital_insurance_company.poliza.dominio.puertos.ConsultaCotizacionPort;
import com.digital_insurance_company.poliza.dominio.puertos.PolizaRepository;

class PolizaApplicationServiceTest {

    private static final LocalDate INICIO = LocalDate.of(2026, 1, 1);
    private static final LocalDate FIN = LocalDate.of(2026, 12, 31);

    /**
     * Fake del puerto secundario PolizaRepository: un Map en vez de una base de
     * datos.
     */
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
    }

    /**
     * Fake del puerto secundario ConsultaCotizacionPort: respuestas fijadas por
     * el test.
     */
    private static class ConsultaCotizacionFake implements ConsultaCotizacionPort {

        final Map<UUID, DatosCotizacionParaEmision> cotizaciones = new HashMap<>();

        @Override
        public Optional<DatosCotizacionParaEmision> buscarParaEmision(UUID cotizacionId) {
            return Optional.ofNullable(cotizaciones.get(cotizacionId));
        }
    }

    private final PolizaRepositoryFake repositorio = new PolizaRepositoryFake();
    private final ConsultaCotizacionFake consultaCotizacion = new ConsultaCotizacionFake();
    private final PolizaApplicationService casosDeUso
            = new PolizaApplicationService(repositorio, consultaCotizacion, new PolizaFactory());

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
        assertThrows(IllegalArgumentException.class,
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
        PolizaNoEncontradaException excepcion = assertThrows(PolizaNoEncontradaException.class,
                () -> casosDeUso.consultarPoliza(UUID.randomUUID()));
        assertNotNull(excepcion);
    }

}
