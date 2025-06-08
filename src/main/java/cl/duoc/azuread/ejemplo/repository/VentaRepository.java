package cl.duoc.azuread.ejemplo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.duoc.azuread.ejemplo.model.Venta;

public interface VentaRepository extends JpaRepository<Venta, Long> {  
    List<Venta> findByCliente(String cliente);
    
}
