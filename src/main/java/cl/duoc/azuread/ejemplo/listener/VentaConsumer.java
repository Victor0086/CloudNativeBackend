package cl.duoc.azuread.ejemplo.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cl.duoc.azuread.ejemplo.dto.VentaDTO;
import cl.duoc.azuread.ejemplo.model.Venta;
import cl.duoc.azuread.ejemplo.service.VentaService;

@Component
public class VentaConsumer {

    @Autowired
    private VentaService ventaService;

    @RabbitListener(queues = "ventas")
    public void recibirVenta(VentaDTO ventaDTO) {
        System.out.println("Venta recibida desde RabbitMQ");

        try {
            Venta venta = ventaService.convertirDesdeDTO(ventaDTO);
            ventaService.crearVenta(venta);
            System.out.println("Venta guardada correctamente en la BD");
        } catch (Exception e) {
            System.err.println(" Error al guardar la venta: " + e.getMessage());
        }
    }
}
