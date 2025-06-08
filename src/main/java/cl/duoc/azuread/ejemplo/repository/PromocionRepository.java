package cl.duoc.azuread.ejemplo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.duoc.azuread.ejemplo.model.Promocion;

@Repository
public interface PromocionRepository extends JpaRepository<Promocion, Long> {}

