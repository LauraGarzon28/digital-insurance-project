package com.auth_service.seguridad;

import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.auth_service.usuario.Rol;
import com.auth_service.usuario.Usuario;
import com.auth_service.usuario.UsuarioRepository;

@Component
public class AdminInicial implements CommandLineRunner {

    private final UsuarioRepository usuarios;
    private final PasswordEncoder encoder;
    private final String adminPassword;

    public AdminInicial(UsuarioRepository usuarios, PasswordEncoder encoder,
            @Value("${admin.password:Admin12345}") String adminPassword) {
        this.usuarios = usuarios;
        this.encoder = encoder;
        this.adminPassword = adminPassword;
    }

    @Override
    public void run(String... args) {
        if (!usuarios.existsByUsername("admin")) {
            usuarios.save(new Usuario("admin", "admin@digitalinsurance.com",
                    encoder.encode(adminPassword), Set.of(Rol.ADMIN)));
        }
    }
}