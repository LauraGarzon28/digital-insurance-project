package com.clientes_service.cliente.aplicacion;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.clientes_service.cliente.dominio.Cliente;
import com.clientes_service.cliente.dominio.ClienteFactory;

/**
 * Pruebas del caso de uso con un fake del repositorio: sin Spring ni base de datos.
 * Cubren la regla de unicidad por documento y la delegación a la factory.
 */
class ClienteApplicationServiceTest {

    private static class RepositorioClientesFake implements RepositorioClientes {

        private final Map<UUID, Cliente> almacen = new HashMap<>();

        @Override
        public Cliente guardar(Cliente cliente) {
            almacen.put(cliente.id(), cliente);
            return cliente;
        }

        @Override
        public Optional<Cliente> buscarPorId(UUID id) {
            return Optional.ofNullable(almacen.get(id));
        }

        @Override
        public boolean existePorNumeroDocumento(String numeroDocumento) {
            return almacen.values().stream()
                    .anyMatch(cliente -> cliente.numeroDocumento().equals(numeroDocumento));
        }
    }

    private final RepositorioClientesFake repositorio = new RepositorioClientesFake();
    private final ClienteUseCase casosDeUso =
            new ClienteApplicationService(new ClienteFactory(), repositorio);

    @Test
    void registraUnClienteNuevoYLoDejaDisponible() {
        Cliente cliente = casosDeUso.registrar(
                "Ana Pérez", "CC", "10203040", "ana@ejemplo.com", "3001234567");

        assertTrue(repositorio.buscarPorId(cliente.id()).isPresent());
        assertEquals("10203040", cliente.numeroDocumento());
    }

    @Test
    void rechazaUnDocumentoYaRegistrado() {
        casosDeUso.registrar("Ana Pérez", "CC", "10203040", "ana@ejemplo.com", "3001234567");

        ClienteYaRegistradoException excepcion = assertThrows(ClienteYaRegistradoException.class,
                () -> casosDeUso.registrar("Otro Nombre", "CC", "10203040", "otro@ejemplo.com", "3009999999"));

        assertEquals("Ya existe un cliente con número de documento 10203040", excepcion.getMessage());
    }

    @Test
    void dosClientesConDocumentosDistintosCoexisten() {
        casosDeUso.registrar("Ana Pérez", "CC", "111", "ana@ejemplo.com", "3001234567");
        casosDeUso.registrar("Luis Gómez", "CC", "222", "luis@ejemplo.com", "3007654321");

        assertTrue(repositorio.existePorNumeroDocumento("111"));
        assertTrue(repositorio.existePorNumeroDocumento("222"));
    }

    @Test
    void buscarPorIdDevuelveVacioSiNoExiste() {
        assertTrue(casosDeUso.buscarPorId(UUID.randomUUID()).isEmpty());
    }
}
