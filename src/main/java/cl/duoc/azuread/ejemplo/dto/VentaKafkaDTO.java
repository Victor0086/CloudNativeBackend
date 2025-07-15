package cl.duoc.azuread.ejemplo.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

/**
 * DTO para mensajes del topic "ventas" en Kafka
 * Representa una transacción de venta de artículos informáticos
 */
@Data
public class VentaKafkaDTO implements Serializable {
    
    private Long ventaId;
    private String cliente;
    private Double total;
    private LocalDateTime fecha;
    private List<ProductoVentaKafkaDTO> productos;
    private String tipoTransaccion; // "NUEVA", "CANCELADA", "MODIFICADA"
    
    @Data
    public static class ProductoVentaKafkaDTO implements Serializable {
        private Long productoId;
        private String nombreProducto;
        private String categoria; // "LAPTOP", "MOUSE", "TECLADO", "MONITOR", etc.
        private Integer cantidad;
        private Double precioUnitario;
        private Double subtotal;
    }
}
