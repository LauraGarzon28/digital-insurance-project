package com.auth_service.seguridad;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.auth_service.usuario.Usuario;

@Service
public class JwtService {

    private final JwtEncoder encoder;
    private final long expiracionSegundos;

    public JwtService(JwtEncoder encoder,
            @Value("${jwt.expiracion-segundos:3600}") long expiracionSegundos) {
        this.encoder = encoder;
        this.expiracionSegundos = expiracionSegundos;
    }

    public String generar(Usuario usuario) {
        Instant ahora = Instant.now();
        JwtClaimsSet.Builder claims = JwtClaimsSet.builder()
                .issuer("digital-insurance-auth")
                .subject(usuario.getId().toString())
                .issuedAt(ahora)
                .expiresAt(ahora.plusSeconds(expiracionSegundos))
                .claim("username", usuario.getUsername())
                .claim("roles", usuario.getRoles().stream().map(Enum::name).toList());
        if (usuario.getClienteId() != null) {
            claims.claim("clienteId", usuario.getClienteId().toString());
        }
        JwsHeader header = JwsHeader.with(SignatureAlgorithm.RS256)
                .keyId(JwtConfig.KEY_ID)
                .build();
        return encoder.encode(JwtEncoderParameters.from(header, claims.build())).getTokenValue();
    }

    public long getExpiracionSegundos() {
        return expiracionSegundos;
    }
}