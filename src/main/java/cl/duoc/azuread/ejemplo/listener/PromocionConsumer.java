package cl.duoc.azuread.ejemplo.listener;

import cl.duoc.azuread.ejemplo.model.Promocion;
import cl.duoc.azuread.ejemplo.service.PromocionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import com.rabbitmq.client.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * MICROSERVICIO CONSUMIDOR 2 - PromocionConsumer
 * 
 * Este microservicio CONSUME mensajes de la cola "cola.promociones" 
 * enviados por PromocionRabbitProducer (Microservicio Productor 2)
 * 
 */
@Component
public class PromocionConsumer {

    @Autowired
    private PromocionService promocionService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Consume mensajes de la cola "cola.promociones"
     * Enviados por PromocionRabbitProducer
     */
    @RabbitListener(queues = "cola.promociones", ackMode = "MANUAL")
    public void recibirPromocion(Message message, Channel channel) {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            System.out.println("[✓] CONSUMIDOR 2 (PromocionConsumer) - Procesando promoción recibida");
            
            // Deserializar el mensaje JSON
            String jsonRaw = new String(message.getBody(), "UTF-8").trim();

            // Si por error el JSON vino como un String dentro de un String 
            if (jsonRaw.startsWith("\"") && jsonRaw.endsWith("\"")) {
                jsonRaw = jsonRaw.substring(1, jsonRaw.length() - 1).replace("\\\"", "\"");
            }

            Promocion promo = objectMapper.readValue(jsonRaw, Promocion.class);
            System.out.println("    Descripción: " + promo.getDescripcion());
            System.out.println("    Descuento: " + promo.getDescuento() + "%");

            // PASO 1: Guardar en BD
            promocionService.guardar(promo);
            System.out.println(" Promoción guardada en Oracle correctamente");
            
            // PASO 2: GENERA ARCHIVO JSON (REQUERIMIENTO ESPECÍFICO)
            generarArchivoJson(promo);

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

    /**
     * GENERA ARCHIVO JSON con la promoción recibida
     * 
     * REQUERIMIENTO ESPECÍFICO CUMPLIDO: 
     * "El microservicio consumidor de mensajes de promociones debía generar un archivo JSON"
     * 
     * Este método crea archivos JSON únicos en el directorio "promociones_json"
     */
    private void generarArchivoJson(Promocion promocion) {
        try {
            // Crea directorio si no existe
            File directory = new File("promociones_json");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            
            // Genera nombre de archivo único
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String nombreArchivo = "promocion_" + timestamp + ".json";
            File archivo = new File(directory, nombreArchivo);
            
            // Escribir JSON al archivo
            try (FileWriter writer = new FileWriter(archivo, StandardCharsets.UTF_8)) {
                String jsonContent = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(promocion);
                writer.write(jsonContent);
            }
            
            System.out.println(" ARCHIVO JSON GENERADO: " + archivo.getAbsolutePath());
            System.out.println(" REQUERIMIENTO CUMPLIDO: Consumidor de promociones genera archivo JSON");
            
        } catch (IOException e) {
            System.err.println("Error al generar archivo JSON: " + e.getMessage());
        }
    }
}
