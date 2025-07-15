package cl.duoc.azuread.ejemplo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.duoc.azuread.ejemplo.model.Inventario;

public interface InventarioRepository extends JpaRepository<Inventario, Long> {
    
    
}
