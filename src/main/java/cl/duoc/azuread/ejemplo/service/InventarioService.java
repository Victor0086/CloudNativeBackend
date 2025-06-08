package cl.duoc.azuread.ejemplo.service;

import cl.duoc.azuread.ejemplo.model.Inventario;
import cl.duoc.azuread.ejemplo.repository.InventarioRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class InventarioService {

    private final InventarioRepository inventarioRepository;

    public InventarioService(InventarioRepository inventarioRepository) {
        this.inventarioRepository = inventarioRepository;
    }

    public List<Inventario> listarTodos() {
        return inventarioRepository.findAll();
    }

    public Inventario guardar(Inventario inventario) {
        return inventarioRepository.save(inventario);
    }

    public Inventario buscarPorId(Long id) {
        return inventarioRepository.findById(id).orElse(null);
    }

    public void eliminar(Long id) {
        inventarioRepository.deleteById(id);
    }

    public Inventario actualizar(Long id, Inventario inventario) {
        Inventario existente = buscarPorId(id);
        if (existente != null) {
            existente.setNombreProducto(inventario.getNombreProducto());
            existente.setStock(inventario.getStock());
            return inventarioRepository.save(existente);
        }
        return null;
    }
}
