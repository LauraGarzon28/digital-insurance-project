package com.auth_service.web;

import java.net.URI;
import java.util.Map;
import java.util.List;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth_service.seguridad.JwtService;
import com.auth_service.usuario.CredencialesInvalidasException;
import com.auth_service.usuario.Rol;
import com.auth_service.usuario.Usuario;
import com.auth_service.usuario.UsuarioDuplicadoException;
import com.auth_service.usuario.UsuarioRepository;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UsuarioRepository usuarios;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;
    private final RSAKey rsaKey;

    public AuthController(UsuarioRepository usuarios, PasswordEncoder encoder,
            JwtService jwtService, RSAKey rsaKey) {
        this.usuarios = usuarios;
        this.encoder = encoder;
        this.jwtService = jwtService;
        this.rsaKey = rsaKey;
    }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        Usuario usuario = usuarios.findByUsername(request.username())
                .filter(Usuario::isActivo)
                .filter(u -> encoder.matches(request.password(), u.getPasswordHash()))
                .orElseThrow(CredencialesInvalidasException::new);   // mismo error si falla usuario o clave
        return new TokenResponse(jwtService.generar(usuario), "Bearer", jwtService.getExpiracionSegundos());
    }

    @PostMapping("/register")
    public ResponseEntity<UsuarioResponse> registrar(@Valid @RequestBody RegistroRequest request) {
        return crear(request.username(), request.correo(), request.password(), Set.of(Rol.CLIENTE));
    }

    @PostMapping("/usuarios")
    public ResponseEntity<UsuarioResponse> crearInterno(@Valid @RequestBody CrearUsuarioRequest request) {
        return crear(request.username(), request.correo(), request.password(), request.roles());
    }

    @GetMapping("/jwks")
    public Map<String, Object> jwks() {
        return new JWKSet(rsaKey.toPublicJWK()).toJSONObject();   // solo la parte pública
    }

    @GetMapping("/usuarios/ajustadores")
    public List<UsuarioResponse> listarAjustadores() {
        return usuarios.findDistinctByRoles(Rol.AJUSTADOR).stream()
                .map(usuario -> new UsuarioResponse(
                        usuario.getId(),
                        usuario.getUsername(),
                        usuario.getCorreo(),
                        usuario.getRoles()))
                .toList();
    }

    private ResponseEntity<UsuarioResponse> crear(String username, String correo,
            String password, Set<Rol> roles) {
        if (usuarios.existsByUsername(username)) {
            throw new UsuarioDuplicadoException("nombre de usuario");
        }
        if (usuarios.existsByCorreo(correo)) {
            throw new UsuarioDuplicadoException("correo");
        }
        Usuario guardado = usuarios.save(new Usuario(username, correo, encoder.encode(password), roles));
        return ResponseEntity.created(URI.create("/auth/usuarios/" + guardado.getId()))
                .body(new UsuarioResponse(guardado.getId(), guardado.getUsername(),
                        guardado.getCorreo(), guardado.getRoles()));
    }
}