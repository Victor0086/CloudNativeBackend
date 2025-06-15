package cl.duoc.azuread.ejemplo.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.duoc.azuread.ejemplo.model.ProductoVendido;
import cl.duoc.azuread.ejemplo.model.Venta;
import cl.duoc.azuread.ejemplo.repository.ProductoVendidoRepository;
import cl.duoc.azuread.ejemplo.repository.VentaRepository;

@Service
public class VentaService {

    @Autowired
    private VentaRepository ventaRepo;

    @Autowired
    private ProductoVendidoRepository productoVendidoRepo;

    public List<Venta> obtenerTodas() {
        return ventaRepo.findAll();
    }

    public Venta crearVenta(Venta venta) {
        System.out.println("Creando venta para cliente: " + venta.getCliente());
        System.out.println("Productos recibidos:");
        if (venta.getProductos() != null) {
            venta.getProductos().forEach(p -> {
                System.out.println("Producto: " + p.getNombreProducto() + " - Precio: " + p.getPrecioUnitario());
                p.setVenta(venta); // 👈 asocia producto a venta
            });
        } else {
            System.out.println("¡No hay productos en la venta!");
        }

        venta.setFecha(LocalDateTime.now());
        return ventaRepo.save(venta); // debería guardar cascada
    }


    public Venta buscarPorId(Long id) {
        return ventaRepo.findById(id).orElse(null);
    }

    public List<Venta> buscarPorCliente(String email) {
        return ventaRepo.findByCliente(email);
    }
    public boolean eliminarVenta(Long id) {
        if (ventaRepo.existsById(id)) {
            ventaRepo.deleteById(id);
            return true;
        }
        return false;
    }        
}
