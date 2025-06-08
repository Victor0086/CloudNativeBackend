package cl.duoc.azuread.ejemplo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.duoc.azuread.ejemplo.model.ProductoVendido;

public interface ProductoVendidoRepository extends JpaRepository<ProductoVendido, Long> {

    
}
