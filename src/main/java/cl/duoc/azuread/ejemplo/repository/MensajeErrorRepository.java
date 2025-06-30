package cl.duoc.azuread.ejemplo.repository;

import cl.duoc.azuread.ejemplo.model.MensajeError;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MensajeErrorRepository extends JpaRepository<MensajeError, Long> {
}
