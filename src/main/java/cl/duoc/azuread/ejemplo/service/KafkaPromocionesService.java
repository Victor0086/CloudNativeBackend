package cl.duoc.azuread.ejemplo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import cl.duoc.azuread.ejemplo.config.KafkaConsumerConfig;
import cl.duoc.azuread.ejemplo.dto.VentaKafkaDTO;
import cl.duoc.azuread.ejemplo.dto.StockUpdateDTO;
import cl.duoc.azuread.ejemplo.dto.PromocionKafkaDTO;
import cl.duoc.azuread.ejemplo.dto.PromocionKafkaDTO.ProductoPromocionDTO;
import cl.duoc.azuread.ejemplo.model.Promocion;
import cl.duoc.azuread.ejemplo.model.Inventario;


import java.time.LocalDateTime;
import java.sql.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * MICROSERVICIO DE PROMOCIONES - KafkaPromocionesService
 * 
 * Consume mensajes de los tópicos "ventas" y "stock"
 * Genera promociones automáticas basadas en stock alto y patrones de venta
 * Permite activación On Demand de promociones
 */
@Service
public class KafkaPromocionesService {
    
    @Autowired
    private PromocionService promocionService;
    
    @Autowired
    private InventarioService inventarioService;
    
    @Autowired
    private KafkaTemplate<String, PromocionKafkaDTO> kafkaTemplate;
    
    /**
     * Consume mensajes del tópico "stock" para generar promociones automáticas
     */
    @KafkaListener(topics = KafkaConsumerConfig.TOPIC_STOCK, 
                   groupId = "promociones-group",
                   containerFactory = "stockUpdateListenerContainerFactory")
    public void procesarActualizacionStock(StockUpdateDTO stockUpdate, Acknowledgment ack) {
        try {
            System.out.println("PROMOCIONES- Analizando stock: " + stockUpdate.getNombreProducto());
            
            // Generar promoción por stock alto
            if (stockUpdate.getStockActual() > 20 && "DISPONIBLE".equals(stockUpdate.getEstado())) {
                generarPromocionStockAlto(stockUpdate);
            }
            
            // Generar promoción por producto agotado (para productos relacionados)
            if ("AGOTADO".equals(stockUpdate.getEstado())) {
                generarPromocionProductosRelacionados(stockUpdate);
            }
            
            ack.acknowledge();
            
        } catch (Exception e) {
            System.err.println("Error procesando promoción por stock: " + e.getMessage());
        }
    }
    
    /**
     * Consume mensajes del tópico "ventas" para análisis de tendencias
     */
    @KafkaListener(topics = KafkaConsumerConfig.TOPIC_VENTAS, 
                   groupId = "promociones-ventas-group",
                   containerFactory = "ventaKafkaListenerContainerFactory")
    public void analizarTendenciaVentas(VentaKafkaDTO venta, Acknowledgment ack) {
        try {
            System.out.println("PROMOCIONES-Analizando venta: " + venta.getVentaId());
            
            // Generar promociones cruzadas basadas en productos vendidos juntos
            if (venta.getProductos().size() > 1) {
                generarPromocionVentaCruzada(venta);
            }
            
            ack.acknowledge();
            
        } catch (Exception e) {
            System.err.println("Error analizando venta para promociones: " + e.getMessage());
        }
    }
    
    /**
     * Genera promoción automática por stock alto
     */
    private void generarPromocionStockAlto(StockUpdateDTO stockUpdate) {
        try {
            // Crear promoción en base de datos
            Promocion promocion = new Promocion();
            promocion.setDescripcion("¡Oferta especial! " + stockUpdate.getNombreProducto() + 
                                   " - Descuento por stock disponible");
            promocion.setDescuento(15.0); // 15% de descuento
            promocion.setFechaInicio(new Date(System.currentTimeMillis()));
            promocion.setFechaFin(Date.valueOf(LocalDateTime.now().plusDays(7).toLocalDate()));
            
            Promocion promocionGuardada = promocionService.guardar(promocion);
            
            // Crear DTO para Kafka
            PromocionKafkaDTO promocionKafka = new PromocionKafkaDTO();
            promocionKafka.setPromocionId(promocionGuardada.getId());
            promocionKafka.setTitulo("Oferta Stock Alto - " + stockUpdate.getCategoria());
            promocionKafka.setDescripcion(promocion.getDescripcion());
            promocionKafka.setDescuento(promocion.getDescuento());
            promocionKafka.setTipoPromocion("ALTO_STOCK");
            promocionKafka.setFechaInicio(promocion.getFechaInicio().toLocalDate().atStartOfDay());
            promocionKafka.setFechaFin(promocion.getFechaFin().toLocalDate().atTime(23, 59, 59));
            promocionKafka.setCondiciones("Válido mientras dure el stock. Stock actual: " + stockUpdate.getStockActual());
            promocionKafka.setEstado("ACTIVA");
            promocionKafka.setTrigger("AUTO_STOCK");
            
            // Agregar producto a la promoción
            List<ProductoPromocionDTO> productos = new ArrayList<>();
            ProductoPromocionDTO producto = new ProductoPromocionDTO();
            producto.setProductoId(stockUpdate.getProductoId());
            producto.setNombreProducto(stockUpdate.getNombreProducto());
            producto.setCategoria(stockUpdate.getCategoria());
            producto.setStockDisponible(stockUpdate.getStockActual());
            productos.add(producto);
            promocionKafka.setProductosIncluidos(productos);
            
            // Publicar al tópico de promociones
            kafkaTemplate.send(KafkaConsumerConfig.TOPIC_PROMOCIONES, 
                              promocionGuardada.getId().toString(), 
                              promocionKafka);
            
            System.out.println("PROMOCIÓN CREADA - Stock Alto: " + stockUpdate.getNombreProducto() + 
                             " (Stock: " + stockUpdate.getStockActual() + ")");
            
        } catch (Exception e) {
            System.err.println("Error generando promoción por stock alto: " + e.getMessage());
        }
    }
    
    /**
     * Genera promoción para productos relacionados cuando uno se agota
     */
    private void generarPromocionProductosRelacionados(StockUpdateDTO stockUpdate) {
        try {
            // Buscar productos de la misma categoría con stock disponible
            List<Inventario> productosRelacionados = inventarioService.listarTodos().stream()
                .filter(inv -> inv.getStock() > 0)
                .filter(inv -> !inv.getNombreProducto().equals(stockUpdate.getNombreProducto()))
                .filter(inv -> sonProductosRelacionados(inv.getNombreProducto(), stockUpdate.getCategoria()))
                .limit(3) // Máximo 3 productos
                .collect(Collectors.toList());
            
            if (!productosRelacionados.isEmpty()) {
                Promocion promocion = new Promocion();
                promocion.setDescripcion("Productos alternativos disponibles - " + stockUpdate.getCategoria());
                promocion.setDescuento(10.0);
                promocion.setFechaInicio(new Date(System.currentTimeMillis()));
                promocion.setFechaFin(Date.valueOf(LocalDateTime.now().plusDays(3).toLocalDate()));
                
                Promocion promocionGuardada = promocionService.guardar(promocion);
                
                // Crear DTO para Kafka
                PromocionKafkaDTO promocionKafka = new PromocionKafkaDTO();
                promocionKafka.setPromocionId(promocionGuardada.getId());
                promocionKafka.setTitulo("Alternativas Disponibles - " + stockUpdate.getCategoria());
                promocionKafka.setDescripcion("Productos similares disponibles con descuento");
                promocionKafka.setDescuento(10.0);
                promocionKafka.setTipoPromocion("LIQUIDACION");
                promocionKafka.setFechaInicio(promocion.getFechaInicio().toLocalDate().atStartOfDay());
                promocionKafka.setFechaFin(promocion.getFechaFin().toLocalDate().atTime(23, 59, 59));
                promocionKafka.setCondiciones("Producto " + stockUpdate.getNombreProducto() + " agotado. Alternativas con descuento.");
                promocionKafka.setEstado("ACTIVA");
                promocionKafka.setTrigger("AUTO_STOCK");
                
                // Agregar productos relacionados
                List<ProductoPromocionDTO> productos = productosRelacionados.stream()
                    .map(inv -> {
                        ProductoPromocionDTO prod = new ProductoPromocionDTO();
                        prod.setProductoId(Long.valueOf(inv.getId()));
                        prod.setNombreProducto(inv.getNombreProducto());
                        prod.setCategoria(stockUpdate.getCategoria());
                        prod.setStockDisponible(inv.getStock());
                        prod.setPrecioOriginal(inv.getPrecio());
                        prod.setPrecioConDescuento(inv.getPrecio() * 0.9); // 10% descuento
                        return prod;
                    })
                    .collect(Collectors.toList());
                
                promocionKafka.setProductosIncluidos(productos);
                
                kafkaTemplate.send(KafkaConsumerConfig.TOPIC_PROMOCIONES, 
                                  promocionGuardada.getId().toString(), 
                                  promocionKafka);
                
                System.out.println("PROMOCIÓN ALTERNATIVAS- Creada por agotamiento de: " + stockUpdate.getNombreProducto());
            }
            
        } catch (Exception e) {
            System.err.println("Error generando promoción de alternativas: " + e.getMessage());
        }
    }
    
    /**
     * Genera promoción de venta cruzada
     */
    private void generarPromocionVentaCruzada(VentaKafkaDTO venta) {
        try {
            // Lógica simple: si compran laptop, ofrecer descuento en accesorios
            boolean tieneComputadora = venta.getProductos().stream()
                .anyMatch(p -> "LAPTOP".equals(p.getCategoria()) || "CPU".equals(p.getCategoria()));
            
            if (tieneComputadora) {
                // Buscar accesorios disponibles
                List<Inventario> accesorios = inventarioService.listarTodos().stream()
                    .filter(inv -> inv.getStock() > 0)
                    .filter(inv -> esAccesorio(inv.getNombreProducto()))
                    .limit(5)
                    .collect(Collectors.toList());
                
                if (!accesorios.isEmpty()) {
                    Promocion promocion = new Promocion();
                    promocion.setDescripcion("Combo accesorios para tu nueva computadora");
                    promocion.setDescuento(20.0);
                    promocion.setFechaInicio(new Date(System.currentTimeMillis()));
                    promocion.setFechaFin(Date.valueOf(LocalDateTime.now().plusDays(5).toLocalDate()));
                    
                    Promocion promocionGuardada = promocionService.guardar(promocion);
                    
                    PromocionKafkaDTO promocionKafka = new PromocionKafkaDTO();
                    promocionKafka.setPromocionId(promocionGuardada.getId());
                    promocionKafka.setTitulo("Combo Accesorios");
                    promocionKafka.setDescripcion("Complementa tu compra con accesorios esenciales");
                    promocionKafka.setDescuento(20.0);
                    promocionKafka.setTipoPromocion("VENTA_CRUZADA");
                    promocionKafka.setFechaInicio(promocion.getFechaInicio().toLocalDate().atStartOfDay());
                    promocionKafka.setFechaFin(promocion.getFechaFin().toLocalDate().atTime(23, 59, 59));
                    promocionKafka.setCondiciones("Válido para clientes que compraron computadoras");
                    promocionKafka.setEstado("ACTIVA");
                    promocionKafka.setTrigger("AUTO_VENTA");
                    
                    List<ProductoPromocionDTO> productos = accesorios.stream()
                        .map(inv -> {
                            ProductoPromocionDTO prod = new ProductoPromocionDTO();
                            prod.setProductoId(Long.valueOf(inv.getId()));
                            prod.setNombreProducto(inv.getNombreProducto());
                            prod.setCategoria("ACCESORIOS");
                            prod.setStockDisponible(inv.getStock());
                            prod.setPrecioOriginal(inv.getPrecio());
                            prod.setPrecioConDescuento(inv.getPrecio() * 0.8);
                            return prod;
                        })
                        .collect(Collectors.toList());
                    
                    promocionKafka.setProductosIncluidos(productos);
                    
                    kafkaTemplate.send(KafkaConsumerConfig.TOPIC_PROMOCIONES, 
                                      promocionGuardada.getId().toString(), 
                                      promocionKafka);
                    
                    System.out.println("PROMOCIÓN CRUZADA- Creada para venta: " + venta.getVentaId());
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error generando promoción cruzada: " + e.getMessage());
        }
    }
    
    /**
     * Método público para activar promociones On Demand
     */
    public void activarPromocionOnDemand(String categoria, Double descuento, int diasValidez) {
        try {
            System.out.println("ON DEMAND- Activando promoción para categoría: " + categoria);
            
            List<Inventario> productos = inventarioService.listarTodos().stream()
                .filter(inv -> inv.getStock() > 0)
                .filter(inv -> categoriaCoincide(inv.getNombreProducto(), categoria))
                .collect(Collectors.toList());
            
            if (!productos.isEmpty()) {
                Promocion promocion = new Promocion();
                promocion.setDescripcion("Promoción especial On Demand - " + categoria);
                promocion.setDescuento(descuento);
                promocion.setFechaInicio(new Date(System.currentTimeMillis()));
                promocion.setFechaFin(Date.valueOf(LocalDateTime.now().plusDays(diasValidez).toLocalDate()));
                
                Promocion promocionGuardada = promocionService.guardar(promocion);
                
                PromocionKafkaDTO promocionKafka = new PromocionKafkaDTO();
                promocionKafka.setPromocionId(promocionGuardada.getId());
                promocionKafka.setTitulo("Promoción On Demand - " + categoria);
                promocionKafka.setDescripcion("Activación manual de promoción especial");
                promocionKafka.setDescuento(descuento);
                promocionKafka.setTipoPromocion("ON_DEMAND");
                promocionKafka.setFechaInicio(promocion.getFechaInicio().toLocalDate().atStartOfDay());
                promocionKafka.setFechaFin(promocion.getFechaFin().toLocalDate().atTime(23, 59, 59));
                promocionKafka.setCondiciones("Promoción activada manualmente por administrador");
                promocionKafka.setEstado("ACTIVA");
                promocionKafka.setTrigger("ON_DEMAND");
                
                List<ProductoPromocionDTO> productosPromo = productos.stream()
                    .map(inv -> {
                        ProductoPromocionDTO prod = new ProductoPromocionDTO();
                        prod.setProductoId(Long.valueOf(inv.getId()));
                        prod.setNombreProducto(inv.getNombreProducto());
                        prod.setCategoria(categoria);
                        prod.setStockDisponible(inv.getStock());
                        prod.setPrecioOriginal(inv.getPrecio());
                        prod.setPrecioConDescuento(inv.getPrecio() * (1 - descuento/100));
                        return prod;
                    })
                    .collect(Collectors.toList());
                
                promocionKafka.setProductosIncluidos(productosPromo);
                
                kafkaTemplate.send(KafkaConsumerConfig.TOPIC_PROMOCIONES, 
                                  promocionGuardada.getId().toString(), 
                                  promocionKafka);
                
                System.out.println("PROMOCIÓN ON DEMAND- Activada para " + productos.size() + " productos de " + categoria);
            }
            
        } catch (Exception e) {
            System.err.println("Error activando promoción On Demand: " + e.getMessage());
        }
    }
    
    // Métodos utilitarios
    private boolean sonProductosRelacionados(String nombreProducto, String categoria) {
        String nombre = nombreProducto.toLowerCase();
        if (categoria == null) return false;
        
        switch (categoria) {
            case "LAPTOP": return nombre.contains("mouse") || nombre.contains("bolso") || nombre.contains("cargador");
            case "MOUSE": return nombre.contains("mousepad") || nombre.contains("teclado");
            case "TECLADO": return nombre.contains("mouse") || nombre.contains("soporte");
            case "MONITOR": return nombre.contains("cable") || nombre.contains("soporte");
            default: return false;
        }
    }
    
    private boolean esAccesorio(String nombreProducto) {
        String nombre = nombreProducto.toLowerCase();
        return nombre.contains("mouse") || nombre.contains("teclado") || 
               nombre.contains("cable") || nombre.contains("adaptador") ||
               nombre.contains("bolso") || nombre.contains("soporte") ||
               nombre.contains("mousepad") || nombre.contains("auricular");
    }
    
    private boolean categoriaCoincide(String nombreProducto, String categoria) {
        String nombre = nombreProducto.toLowerCase();
        String cat = categoria.toLowerCase();
        
        return nombre.contains(cat) || 
               (cat.contains("laptop") && (nombre.contains("laptop") || nombre.contains("notebook"))) ||
               (cat.contains("accesorios") && esAccesorio(nombre));
    }
}
