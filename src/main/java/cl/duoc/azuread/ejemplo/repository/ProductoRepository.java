package cl.duoc.azuread.ejemplo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.duoc.azuread.ejemplo.model.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
}
