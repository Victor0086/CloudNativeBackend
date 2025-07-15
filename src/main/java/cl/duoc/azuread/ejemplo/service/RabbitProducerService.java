package cl.duoc.azuread.ejemplo.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import cl.duoc.azuread.ejemplo.dto.VentaDTO;

/**
 * MICROSERVICIO PRODUCTOR 1 - RabbitProducerService
 * 
 * Este microservicio se encarga de ENVIAR mensajes a la cola "ventas"
 * Los mensajes serán consumidos por VentaConsumer (Microservicio Consumidor 1)
 * 
 * REQUERIMIENTO: "Dos microservicios que generaran mensajes, uno por cada cola"
 * Este es el PRODUCTOR 1 para la cola "ventas"
 */
@Service
public class RabbitProducerService {

    private final RabbitTemplate rabbitTemplate;

    public RabbitProducerService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Envía una venta a la cola "ventas"
     * Este mensaje será procesado por VentaConsumer
     */
    public void enviarVenta(VentaDTO venta) {
        try {
            rabbitTemplate.convertAndSend("ventas", venta);
            System.out.println("[→] PRODUCTOR 1 (RabbitProducerService) - Venta enviada a cola 'ventas': " + venta.getCliente());
        } catch (Exception e) {
            System.err.println("[✗] Error al enviar venta a RabbitMQ: " + e.getMessage());
        }
    }
}
