package cl.duoc.azuread.ejemplo.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import cl.duoc.azuread.ejemplo.dto.VentaDTO;

@Service
public class RabbitProducerService {

    private final RabbitTemplate rabbitTemplate;

    public RabbitProducerService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void enviarVenta(VentaDTO venta) {
        try {
            rabbitTemplate.convertAndSend("ventas", venta);
            System.out.println(" Venta enviada a RabbitMQ correctamente: " + venta);
        } catch (Exception e) {
            System.err.println(" Error al enviar venta a RabbitMQ: " + e.getMessage());
        }
    }
}
