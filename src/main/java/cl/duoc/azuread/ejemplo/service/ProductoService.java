package cl.duoc.azuread.ejemplo.service;

import java.util.List;
import org.springframework.stereotype.Service;
import cl.duoc.azuread.ejemplo.model.Producto;
import cl.duoc.azuread.ejemplo.repository.ProductoRepository;

@Service
public class ProductoService {

    private final ProductoRepository repo;

    public ProductoService(ProductoRepository repo) {
        this.repo = repo;
    }

    public List<Producto> listarTodos() {
        return repo.findAll();
    }

    public Producto buscarPorId(Long id) {
        return repo.findById(id).orElse(null);
    }

    public Producto guardar(Producto producto) {
        return repo.save(producto);
    }

    public void eliminar(Long id) {
        repo.deleteById(id);
    }
    public Producto actualizar(Long id, Producto producto) {
        Producto existente = buscarPorId(id);
        if (existente != null) {
            existente.setNombre(producto.getNombre());
            existente.setPrecio(producto.getPrecio());
            return repo.save(existente);
        }
        return null;
    }
}
