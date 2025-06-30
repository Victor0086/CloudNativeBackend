package cl.duoc.azuread.ejemplo.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import cl.duoc.azuread.ejemplo.dto.VentaDTO;
import cl.duoc.azuread.ejemplo.model.ProductoVendido;
import cl.duoc.azuread.ejemplo.model.Venta;
import cl.duoc.azuread.ejemplo.repository.ProductoVendidoRepository;
import cl.duoc.azuread.ejemplo.repository.VentaRepository;

@Service
public class VentaService {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private ProductoVendidoRepository productoVendidoRepository;

    public List<Venta> obtenerTodas() {
        return ventaRepository.findAll();
    }

    public Venta crearVenta(Venta venta) {
        System.out.println("Creando venta para cliente: " + venta.getCliente());
        System.out.println("Productos recibidos:");
        if (venta.getProductos() != null) {
            venta.getProductos().forEach(p -> {
                System.out.println("Producto: " + p.getNombreProducto() + " - Precio: " + p.getPrecioUnitario());
                p.setVenta(venta); // asocia producto a venta
            });
        } else {
            System.out.println("¡No hay productos en la venta!");
        }

        venta.setFecha(LocalDateTime.now());
        return ventaRepository.save(venta); // debería guardar en cascada
    }

    public Venta buscarPorId(Long id) {
        return ventaRepository.findById(id).orElse(null);
    }

    public List<Venta> buscarPorCliente(String email) {
        return ventaRepository.findByCliente(email);
    }

    public boolean eliminarVenta(Long id) {
        if (ventaRepository.existsById(id)) {
            ventaRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Método que convierte VentaDTO en Venta
    public Venta convertirDesdeDTO(VentaDTO dto) {
        Venta venta = new Venta();
        venta.setCliente(dto.getCliente());
        venta.setTotal(dto.getTotal());
        venta.setFecha(LocalDateTime.now());

        if (dto.getProductos() != null) {
            List<ProductoVendido> productos = dto.getProductos().stream().map(pdto -> {
                ProductoVendido p = new ProductoVendido();
                p.setNombreProducto(pdto.getNombreProducto());
                p.setPrecioUnitario(pdto.getPrecioUnitario());
                p.setVenta(venta); // relación bidireccional
                return p;
            }).toList();
            venta.setProductos(productos);
        }

        return venta;
    }


      public void guardarVenta(VentaDTO ventaDTO) {
        Venta venta = new Venta();
        venta.setCliente(ventaDTO.getCliente());
        venta.setFecha(LocalDateTime.now());
        venta.setTotal(ventaDTO.getTotal());

        ventaRepository.save(venta); // Primero se guarda la venta para obtener el ID

        List<ProductoVendido> productos = new ArrayList<>();
        for (var productoDTO : ventaDTO.getProductos()) {
            ProductoVendido producto = new ProductoVendido();
            producto.setNombreProducto(productoDTO.getNombreProducto());
            producto.setPrecioUnitario(productoDTO.getPrecioUnitario());
            producto.setVenta(venta); // relación con la venta

            productos.add(producto);
        }

        productoVendidoRepository.saveAll(productos);

        System.out.println("Venta guardada correctamente en la BD");
    }
}
