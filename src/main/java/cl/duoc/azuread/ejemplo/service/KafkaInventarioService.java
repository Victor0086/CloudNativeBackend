package cl.duoc.azuread.ejemplo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import cl.duoc.azuread.ejemplo.config.KafkaConsumerConfig;
import cl.duoc.azuread.ejemplo.dto.VentaKafkaDTO;
import cl.duoc.azuread.ejemplo.dto.StockUpdateDTO;
import cl.duoc.azuread.ejemplo.dto.VentaKafkaDTO.ProductoVentaKafkaDTO;
import cl.duoc.azuread.ejemplo.model.Inventario;


import java.time.LocalDateTime;


/**
 * MICROSERVICIO PROCESAMIENTO DE INVENTARIO - KafkaInventarioService
 * 
 * Consume mensajes del tópico "ventas" y actualiza el inventario
 * Publica actualizaciones al tópico "stock"
 */
@Service
public class KafkaInventarioService {
    
    @Autowired
    private InventarioService inventarioService;
    
    @Autowired
    private KafkaTemplate<String, StockUpdateDTO> kafkaTemplate;
    
    /**
     * Consume mensajes del tópico "ventas" y procesa actualizaciones de inventario
     */
    @KafkaListener(topics = KafkaConsumerConfig.TOPIC_VENTAS, 
                   groupId = "inventario-group",
                   containerFactory = "ventaKafkaListenerContainerFactory")
    public void procesarVenta(VentaKafkaDTO venta, Acknowledgment ack) {
        try {
            System.out.println("INVENTARIO PROCESSOR- Procesando venta: " + venta.getVentaId());
            
            // Procesar cada producto de la venta
            for (ProductoVentaKafkaDTO producto : venta.getProductos()) {
                if ("NUEVA".equals(venta.getTipoTransaccion())) {
                    procesarVentaProducto(producto, venta.getVentaId());
                } else if ("CANCELADA".equals(venta.getTipoTransaccion())) {
                    procesarCancelacionProducto(producto, venta.getVentaId());
                }
            }
            
            // Confirmar procesamiento
            ack.acknowledge();
            System.out.println("INVENTARIO PROCESSOR- Venta procesada exitosamente: " + venta.getVentaId());
            
        } catch (Exception e) {
            System.err.println("Error al procesar venta en inventario: " + e.getMessage());
            e.printStackTrace();
            // No hacer acknowledge para reintentar
        }
    }
    
    /**
     * Procesa la venta de un producto (disminuye stock)
     */
    private void procesarVentaProducto(ProductoVentaKafkaDTO producto, Long ventaId) {
        try {
            // Buscar producto en inventario por nombre (en un caso real sería por ID)
            Inventario inventario = buscarInventarioPorNombre(producto.getNombreProducto());
            
            if (inventario != null) {
                int stockAnterior = inventario.getStock();
                int nuevaCantidad = Math.max(0, stockAnterior - producto.getCantidad());
                
                // Actualizar stock
                inventario.setStock(nuevaCantidad);
                inventarioService.guardar(inventario);
                
                // Crear mensaje para tópico "stock"
                StockUpdateDTO stockUpdate = new StockUpdateDTO();
                stockUpdate.setProductoId(Long.valueOf(inventario.getId()));
                stockUpdate.setNombreProducto(inventario.getNombreProducto());
                stockUpdate.setCategoria(producto.getCategoria());
                stockUpdate.setStockAnterior(stockAnterior);
                stockUpdate.setStockActual(nuevaCantidad);
                stockUpdate.setCantidadVendida(producto.getCantidad());
                stockUpdate.setTipoActualizacion("VENTA");
                stockUpdate.setFechaActualizacion(LocalDateTime.now());
                stockUpdate.setVentaId(ventaId);
                stockUpdate.setStockMinimo(5); // Configurar según negocio
                
                // Determinar estado del stock
                if (nuevaCantidad == 0) {
                    stockUpdate.setEstado("AGOTADO");
                } else if (nuevaCantidad <= stockUpdate.getStockMinimo()) {
                    stockUpdate.setEstado("BAJO_STOCK");
                } else {
                    stockUpdate.setEstado("DISPONIBLE");
                }
                
                // Publicar al tópico "stock"
                kafkaTemplate.send(KafkaConsumerConfig.TOPIC_STOCK, 
                                  inventario.getId().toString(), 
                                  stockUpdate);
                
                System.out.println("STOCK UPDATE- Publicado: " + producto.getNombreProducto() + 
                                 " | Stock: " + stockAnterior + " → " + nuevaCantidad);
                
            } else {
                System.err.println("Producto no encontrado en inventario: " + producto.getNombreProducto());
            }
            
        } catch (Exception e) {
            System.err.println("Error procesando producto: " + producto.getNombreProducto());
            e.printStackTrace();
        }
    }
    
    /**
     * Procesa la cancelación de una venta (aumenta stock)
     */
    private void procesarCancelacionProducto(ProductoVentaKafkaDTO producto, Long ventaId) {
        try {
            Inventario inventario = buscarInventarioPorNombre(producto.getNombreProducto());
            
            if (inventario != null) {
                int stockAnterior = inventario.getStock();
                int nuevaCantidad = stockAnterior + producto.getCantidad();
                
                inventario.setStock(nuevaCantidad);
                inventarioService.guardar(inventario);
                
                // Publicar actualización de stock por cancelación
                StockUpdateDTO stockUpdate = new StockUpdateDTO();
                stockUpdate.setProductoId(Long.valueOf(inventario.getId()));
                stockUpdate.setNombreProducto(inventario.getNombreProducto());
                stockUpdate.setStockAnterior(stockAnterior);
                stockUpdate.setStockActual(nuevaCantidad);
                stockUpdate.setCantidadVendida(-producto.getCantidad()); // Negativo por devolución
                stockUpdate.setTipoActualizacion("CANCELACION");
                stockUpdate.setFechaActualizacion(LocalDateTime.now());
                stockUpdate.setVentaId(ventaId);
                stockUpdate.setEstado("DISPONIBLE");
                
                kafkaTemplate.send(KafkaConsumerConfig.TOPIC_STOCK, 
                                  inventario.getId().toString(), 
                                  stockUpdate);
                
                System.out.println("CANCELACIÓN- Stock restaurado: " + producto.getNombreProducto() + 
                                 " | Stock: " + stockAnterior + " → " + nuevaCantidad);
            }
            
        } catch (Exception e) {
            System.err.println("Error procesando cancelación: " + producto.getNombreProducto());
        }
    }
    
    /**
     * Busca inventario por nombre del producto
     * En una implementación real, se haría por ID del producto
     */
    private Inventario buscarInventarioPorNombre(String nombreProducto) {
        try {
            return inventarioService.listarTodos().stream()
                .filter(inv -> inv.getNombreProducto().equalsIgnoreCase(nombreProducto))
                .findFirst()
                .orElse(null);
        } catch (Exception e) {
            System.err.println("Error buscando inventario: " + e.getMessage());
            return null;
        }
    }
}
