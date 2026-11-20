package com.clientes_service.cliente.infraestructura.salida.persistencia;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "clientes")
public class ClienteJpaEntity {

    @Id
    private UUID id;
    @Column(name = "nombre_completo", nullable = false)
    private String nombreCompleto;
    @Column(name = "tipo_documento", nullable = false, length = 10)
    private String tipoDocumento;
    @Column(name = "numero_documento", nullable = false, unique = true, length = 30)
    private String numeroDocumento;
    @Column(nullable = false)
    private String correo;
    @Column(nullable = false, length = 20)
    private String telefono;

    protected ClienteJpaEntity() {
    }

    public ClienteJpaEntity(UUID id, String nombreCompleto, String tipoDocumento,
            String numeroDocumento, String correo, String telefono) {
        this.id = id;
        this.nombreCompleto = nombreCompleto;
        this.tipoDocumento = tipoDocumento;
        this.numeroDocumento = numeroDocumento;
        this.correo = correo;
        this.telefono = telefono;
    }

    public UUID getId() { return id; }
    public String getNombreCompleto() { return nombreCompleto; }
    public String getTipoDocumento() { return tipoDocumento; }
    public String getNumeroDocumento() { return numeroDocumento; }
    public String getCorreo() { return correo; }
    public String getTelefono() { return telefono; }
}