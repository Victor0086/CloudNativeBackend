package cl.duoc.azuread.ejemplo.listener;

import cl.duoc.azuread.ejemplo.model.Promocion;
import cl.duoc.azuread.ejemplo.service.PromocionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import com.rabbitmq.client.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PromocionConsumer {

    @Autowired
    private PromocionService promocionService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @RabbitListener(queues = "cola.promociones", ackMode = "MANUAL")
    public void recibirPromocion(Message message, Channel channel) {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            // Deserializar directamente el mensaje JSON (limpiando comillas si es necesario)
            String jsonRaw = new String(message.getBody(), "UTF-8").trim();

            // Si por error el JSON vino como un String dentro de un String (con comillas escapadas), lo limpiamos
            if (jsonRaw.startsWith("\"") && jsonRaw.endsWith("\"")) {
                jsonRaw = jsonRaw.substring(1, jsonRaw.length() - 1).replace("\\\"", "\"");
            }

            Promocion promo = objectMapper.readValue(jsonRaw, Promocion.class);
            System.out.println("Recibido en cola promociones: " + promo.getDescripcion());

            promocionService.guardar(promo);
            System.out.println("Promoción guardada en Oracle correctamente");

            channel.basicAck(deliveryTag, false);
            System.out.println("☑ ACK enviado");

        } catch (Exception e) {
            System.err.println("Error al procesar mensaje: " + e.getMessage());
            try {
                channel.basicNack(deliveryTag, false, false); // Rechaza sin requeue
            } catch (Exception ex) {
                System.err.println("Error al enviar NACK: " + ex.getMessage());
            }
            System.out.println(" Mensaje rechazado");
        }
    }
}
