package cl.duoc.azuread.ejemplo.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

/**
 * DTO para mensajes de promociones generadas por Kafka
 * Representa promociones automáticas para artículos informáticos
 */
@Data
public class PromocionKafkaDTO implements Serializable {
    
    private Long promocionId;
    private String titulo;
    private String descripcion;
    private Double descuento; // Porcentaje de descuento
    private String tipoPromocion; // "ALTO_STOCK", "VENTA_CRUZADA", "LIQUIDACION"
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private List<ProductoPromocionDTO> productosIncluidos;
    private String condiciones; // Condiciones de la promoción
    private String estado; // "ACTIVA", "PAUSADA", "EXPIRADA"
    private String trigger; // "AUTO_STOCK", "ON_DEMAND", "SCHEDULED"
    
    @Data
    public static class ProductoPromocionDTO implements Serializable {
        private Long productoId;
        private String nombreProducto;
        private String categoria;
        private Integer stockDisponible;
        private Double precioOriginal;
        private Double precioConDescuento;
    }
}
