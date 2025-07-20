package cl.duoc.azuread.ejemplo.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import cl.duoc.azuread.ejemplo.model.Venta;

@Service
public class VentaRabbitService {

    private final RabbitTemplate rabbitTemplate;

    public VentaRabbitService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void enviarVenta(Venta venta) {
        // "ventas" es el nombre de la cola RabbitMQ
        rabbitTemplate.convertAndSend("ventas", venta);
    }
}