package cl.duoc.azuread.ejemplo.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import cl.duoc.azuread.ejemplo.dto.VentaDTO;
import cl.duoc.azuread.ejemplo.model.Venta;
import cl.duoc.azuread.ejemplo.service.VentaService;

/**
 * MICROSERVICIO CONSUMIDOR 1 - VentaConsumer
 * Consume mensajes de la cola "ventas" enviados por RabbitProducerService
 * Guarda las ventas en la base de datos
 */
@Component
public class VentaConsumer {

    private final VentaService ventaService;

    public VentaConsumer(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    /**
     * Consume mensajes de la cola de ventas
     */
    @RabbitListener(queues = "ventas")
    public void recibirVenta(VentaDTO ventaDTO) {
        try {
            System.out.println("[✓] CONSUMIDOR VENTAS - Procesando venta recibida");
            System.out.println("    Cliente: " + ventaDTO.getCliente());
            System.out.println("    Total: $" + ventaDTO.getTotal());
            System.out.println("    Productos: " + ventaDTO.getProductos().size());

            // Convertir DTO a entidad y guardar en BD
            Venta venta = ventaService.convertirDesdeDTO(ventaDTO);
            Venta ventaGuardada = ventaService.crearVenta(venta);
            
            System.out.println("[✓] Venta guardada correctamente en BD con ID: " + ventaGuardada.getId());

        } catch (Exception e) {
            System.err.println("[✗] Error al procesar venta: " + e.getMessage());
        }
    }
}
