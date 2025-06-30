package cl.duoc.azuread.ejemplo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mensaje_error")
public class MensajeError {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String contenido;

    @Column(name = "fecha_error")
    private LocalDateTime fechaError;

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public LocalDateTime getFechaError() {
        return fechaError;
    }

    public void setFechaError(LocalDateTime fechaError) {
        this.fechaError = fechaError;
    }
}
