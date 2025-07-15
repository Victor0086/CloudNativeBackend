package cl.duoc.azuread.ejemplo.controllers;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.azuread.ejemplo.dto.ProductoDTO;
import cl.duoc.azuread.ejemplo.dto.VentaDTO;
import cl.duoc.azuread.ejemplo.model.Venta;
import cl.duoc.azuread.ejemplo.service.RabbitProducerService;
import cl.duoc.azuread.ejemplo.service.VentaService;
import cl.duoc.azuread.ejemplo.service.KafkaVentasProducerService;

@RestController
@RequestMapping("/ventas")
public class VentaController {

    private final VentaService service;
    private final RabbitProducerService servicioRabbit;
    private final KafkaVentasProducerService kafkaVentasProducer;

    public VentaController(VentaService service, RabbitProducerService servicioRabbit, 
                          KafkaVentasProducerService kafkaVentasProducer) {
        this.service = service;
        this.servicioRabbit = servicioRabbit;
        this.kafkaVentasProducer = kafkaVentasProducer;
    }


    @GetMapping
    public List<Venta> listarVentas() {
        return service.obtenerTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Venta> obtenerPorId(@PathVariable Long id) {
        Venta venta = service.buscarPorId(id);
        return (venta != null) ? ResponseEntity.ok(venta) : ResponseEntity.notFound().build();
    }
    @GetMapping("/cliente/{email}")
    public List<Venta> listarPorCliente(@PathVariable String email) {
        return service.buscarPorCliente(email);
    }


    @PostMapping
    public ResponseEntity<String> registrarVenta(@RequestBody Venta venta) {
        Venta creada = service.crearVenta(venta); // Guarda en BD

        // Convertir a DTO para RabbitMQ
        VentaDTO ventaDTO = new VentaDTO();
        ventaDTO.setCliente(creada.getCliente());
        ventaDTO.setTotal(creada.getTotal());

        List<ProductoDTO> productosDTO = creada.getProductos().stream().map(p -> {
            ProductoDTO pdto = new ProductoDTO();
            pdto.setNombreProducto(p.getNombreProducto());
            pdto.setPrecioUnitario(p.getPrecioUnitario());
            return pdto;
        }).toList();

        ventaDTO.setProductos(productosDTO);

        // Enviar a RabbitMQ (sistema existente)
        servicioRabbit.enviarVenta(ventaDTO);
        
        //  NUEVO: Enviar a Kafka para procesamiento de inventario
        kafkaVentasProducer.publicarVenta(creada);
        
        return ResponseEntity.ok(" Venta registrada y enviada a RabbitMQ y Kafka\n" +
                               "ID Venta: " + creada.getId() + "\n" +
                               " Total: $" + creada.getTotal() + "\n" +
                               " Procesamiento de inventario iniciado");
    }




    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarVenta(@PathVariable Long id) {
        if (service.eliminarVenta(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }    
}
