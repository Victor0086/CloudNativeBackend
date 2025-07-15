package cl.duoc.azuread.ejemplo.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;

/**
 * DTO para mensajes del topic "stock" en Kafka
 * Representa actualizaciones de inventario de artículos informáticos
 */
@Data
public class StockUpdateDTO implements Serializable {
    
    private Long productoId;
    private String nombreProducto;
    private String categoria; // "LAPTOP", "MOUSE", "TECLADO", "MONITOR", etc.
    private Integer stockAnterior;
    private Integer stockActual;
    private Integer cantidadVendida;
    private String tipoActualizacion; // "VENTA", "RESTOCK", "AJUSTE"
    private LocalDateTime fechaActualizacion;
    private Long ventaId; // ID de la venta que causó el cambio (si aplica)
    private String estado; // "DISPONIBLE", "AGOTADO", "BAJO_STOCK"
    private Integer stockMinimo; // Para alertas
}
