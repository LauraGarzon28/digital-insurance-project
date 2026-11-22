package com.auth_service.usuario;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    private UUID id;
    @Column(nullable = false, unique = true, length = 50)
    private String username;
    @Column(nullable = false, unique = true)
    private String correo;
    @Column(name = "password_hash", nullable = false, length = 100)
    private String passwordHash;
    @Column(name = "cliente_id")
    private UUID clienteId;
    @Column(nullable = false)
    private boolean activo = true;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "usuario_roles", joinColumns = @JoinColumn(name = "usuario_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "rol", nullable = false, length = 20)
    private Set<Rol> roles = new HashSet<>();

    protected Usuario() {
        // exigido por JPA
    }

    public Usuario(String username, String correo, String passwordHash, Set<Rol> roles) {
        this.id = UUID.randomUUID();
        this.username = username;
        this.correo = correo;
        this.passwordHash = passwordHash;
        this.roles = new HashSet<>(roles);
    }

    public UUID getId() { return id; }
    public String getUsername() { return username; }
    public String getCorreo() { return correo; }
    public String getPasswordHash() { return passwordHash; }
    public UUID getClienteId() { return clienteId; }
    public boolean isActivo() { return activo; }
    public Set<Rol> getRoles() { return roles; }
}
