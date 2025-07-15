package cl.duoc.azuread.ejemplo.service;

import cl.duoc.azuread.ejemplo.model.Promocion;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

/**
 * MICROSERVICIO PRODUCTOR 2 - PromocionRabbitProducer
 * 
 * Este microservicio se encarga de ENVIAR mensajes a la cola "cola.promociones"
 * Los mensajes serán consumidos por PromocionConsumer (Microservicio Consumidor 2)
 * que además genera archivos JSON
 * 
 * REQUERIMIENTO: "Dos microservicios que generaran mensajes, uno por cada cola"
 * Este es el PRODUCTOR 2 para la cola "cola.promociones"
 */
@Service
public class PromocionRabbitProducer {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public PromocionRabbitProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Envía una promoción a la cola "cola.promociones"
     * Este mensaje será procesado por PromocionConsumer que:
     * 1. Guarda la promoción en la base de datos
     * 2. Genera un archivo JSON 
     */
    public void enviarPromocion(Promocion promocion) {
        try {
            String json = objectMapper.writeValueAsString(promocion);
            rabbitTemplate.convertAndSend("cola.promociones", json);
            System.out.println("[→] PRODUCTOR 2 (PromocionRabbitProducer) - Promoción enviada a cola 'cola.promociones': " + promocion.getDescripcion());
        } catch (Exception e) {
            System.err.println("[✗] Error al enviar promoción: " + e.getMessage());
        }
    }
}
