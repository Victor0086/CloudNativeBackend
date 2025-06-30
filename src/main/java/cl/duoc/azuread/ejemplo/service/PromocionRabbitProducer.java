package cl.duoc.azuread.ejemplo.service;

import cl.duoc.azuread.ejemplo.model.Promocion;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class PromocionRabbitProducer {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public PromocionRabbitProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = new ObjectMapper();
    }

    public void enviarPromocion(Promocion promocion) {
        try {
            String json = objectMapper.writeValueAsString(promocion);
            rabbitTemplate.convertAndSend("cola.promociones", json);
            System.out.println("[→] Promoción enviada a la cola: " + json);
        } catch (Exception e) {
            System.err.println("Error al enviar promoción: " + e.getMessage());
        }
    }
}
