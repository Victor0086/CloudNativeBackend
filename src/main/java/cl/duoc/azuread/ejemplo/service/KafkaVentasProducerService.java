package cl.duoc.azuread.ejemplo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import cl.duoc.azuread.ejemplo.config.KafkaConsumerConfig;
import cl.duoc.azuread.ejemplo.dto.VentaKafkaDTO;
import cl.duoc.azuread.ejemplo.dto.VentaKafkaDTO.ProductoVentaKafkaDTO;
import cl.duoc.azuread.ejemplo.model.Venta;
import cl.duoc.azuread.ejemplo.model.ProductoVendido;
import java.util.List;
import java.util.stream.Collectors;

/**
 * MICROSERVICIO PRODUCTOR DE VENTAS - KafkaVentasProducerService
 * 
 * Publica transacciones de venta al tópico "ventas" de Kafka en tiempo real
 * Será consumido por el microservicio de procesamiento de inventario
 */
@Service
public class KafkaVentasProducerService {
    
    @Autowired
    private KafkaTemplate<String, VentaKafkaDTO> kafkaTemplate;
    
    /**
     * Publica una venta al tópico "ventas" de Kafka
     * @param venta La venta a publicar
     */
    public void publicarVenta(Venta venta) {
        try {
            VentaKafkaDTO ventaKafka = convertirAKafkaDTO(venta);
            
            // Publicar al tópico "ventas"
            kafkaTemplate.send(KafkaConsumerConfig.TOPIC_VENTAS, 
                              venta.getId().toString(), 
                              ventaKafka);
            
            System.out.println(" [KAFKA PRODUCER] Venta publicada al tópico 'ventas': " + venta.getId());
            System.out.println("   Cliente: " + venta.getCliente());
            System.out.println("   Total: $" + venta.getTotal());
            System.out.println("   Productos: " + venta.getProductos().size());
            
        } catch (Exception e) {
            System.err.println("Error al publicar venta a Kafka: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Publica cancelación de venta
     */
    public void publicarCancelacionVenta(Venta venta) {
        try {
            VentaKafkaDTO ventaKafka = convertirAKafkaDTO(venta);
            ventaKafka.setTipoTransaccion("CANCELADA");
            
            kafkaTemplate.send(KafkaConsumerConfig.TOPIC_VENTAS, 
                              venta.getId().toString(), 
                              ventaKafka);
            
            System.out.println("KAFKA PRODUCER Cancelación de venta publicada: " + venta.getId());
            
        } catch (Exception e) {
            System.err.println("Error al publicar cancelación a Kafka: " + e.getMessage());
        }
    }
    
    /**
     * Convierte una entidad Venta a VentaKafkaDTO
     */
    private VentaKafkaDTO convertirAKafkaDTO(Venta venta) {
        VentaKafkaDTO dto = new VentaKafkaDTO();
        dto.setVentaId(venta.getId());
        dto.setCliente(venta.getCliente());
        dto.setTotal(venta.getTotal());
        dto.setFecha(venta.getFecha());
        dto.setTipoTransaccion("NUEVA");
        
        // Convertir productos
        List<ProductoVentaKafkaDTO> productos = venta.getProductos().stream()
            .map(this::convertirProductoAKafkaDTO)
            .collect(Collectors.toList());
        
        dto.setProductos(productos);
        return dto;
    }
    
    /**
     * Convierte ProductoVendido a ProductoVentaKafkaDTO
     */
    private ProductoVentaKafkaDTO convertirProductoAKafkaDTO(ProductoVendido producto) {
        ProductoVentaKafkaDTO dto = new ProductoVentaKafkaDTO();
        dto.setProductoId(producto.getId());
        dto.setNombreProducto(producto.getNombreProducto());
        dto.setCantidad(1); // Por defecto, ajustar según modelo
        dto.setPrecioUnitario(producto.getPrecioUnitario());
        dto.setSubtotal(producto.getPrecioUnitario());
        
        // Determinar categoría basada en el nombre del producto
        dto.setCategoria(determinarCategoria(producto.getNombreProducto()));
        
        return dto;
    }
    
    /**
     * Determina la categoría del producto informático
     */
    private String determinarCategoria(String nombreProducto) {
        String nombre = nombreProducto.toLowerCase();
        
        if (nombre.contains("laptop") || nombre.contains("notebook")) return "LAPTOP";
        if (nombre.contains("mouse") || nombre.contains("ratón")) return "MOUSE";
        if (nombre.contains("teclado") || nombre.contains("keyboard")) return "TECLADO";
        if (nombre.contains("monitor") || nombre.contains("pantalla")) return "MONITOR";
        if (nombre.contains("processor") || nombre.contains("cpu")) return "CPU";
        if (nombre.contains("memoria") || nombre.contains("ram")) return "RAM";
        if (nombre.contains("disco") || nombre.contains("ssd") || nombre.contains("hdd")) return "ALMACENAMIENTO";
        if (nombre.contains("tarjeta") || nombre.contains("gpu")) return "GPU";
        if (nombre.contains("cable") || nombre.contains("adaptador")) return "ACCESORIOS";
        if (nombre.contains("impresora") || nombre.contains("printer")) return "IMPRESORA";
        
        return "OTROS";
    }
}
