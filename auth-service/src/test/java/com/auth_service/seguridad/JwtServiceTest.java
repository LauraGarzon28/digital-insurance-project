package com.auth_service.seguridad;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import com.auth_service.usuario.Rol;
import com.auth_service.usuario.Usuario;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;

class JwtServiceTest {

    @Test
    void generaUnTokenFirmadoQueSeValidaConLaClavePublica() throws Exception {
        KeyPairGenerator generador = KeyPairGenerator.getInstance("RSA");
        generador.initialize(2048);
        var par = generador.generateKeyPair();
        var publica = (RSAPublicKey) par.getPublic();
        var privada = (RSAPrivateKey) par.getPrivate();
        var rsa = new RSAKey.Builder(publica).privateKey(privada).keyID(JwtConfig.KEY_ID).build();
        var servicio = new JwtService(
                new NimbusJwtEncoder(new ImmutableJWKSet<>(new JWKSet(rsa))), 60);
        var usuario = new Usuario("ana", "ana@example.com", "hash", Set.of(Rol.ASESOR));

        Jwt jwt = NimbusJwtDecoder.withPublicKey(publica).build().decode(servicio.generar(usuario));

        assertEquals(usuario.getId().toString(), jwt.getSubject());
        assertEquals(List.of("ASESOR"), jwt.getClaimAsStringList("roles"));
    }
}