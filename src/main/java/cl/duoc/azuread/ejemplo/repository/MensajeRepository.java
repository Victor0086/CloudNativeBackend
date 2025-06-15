package cl.duoc.azuread.ejemplo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.duoc.azuread.ejemplo.model.Mensaje;

public interface MensajeRepository extends JpaRepository<Mensaje, Long> {
    
}
