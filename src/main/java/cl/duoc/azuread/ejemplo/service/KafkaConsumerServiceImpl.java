package cl.duoc.azuread.ejemplo.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import cl.duoc.azuread.ejemplo.config.KafkaConsumerConfig;
import cl.duoc.azuread.ejemplo.dto.NotificacionDTO;

@Service
public class KafkaConsumerServiceImpl implements KafkaConsumerService {

    @Override
    @KafkaListener(id = "notificacionListener", topics = KafkaConsumerConfig.TOPIC,
     groupId = KafkaConsumerConfig.CONSUMER_GROUP_ID)
     public void consumirNotificaciones(NotificacionDTO notificacion, Acknowledgment ack) {
        try {
            System.out.println("[✓] Mensaje Condumido: " + notificacion.toString());
            Thread.sleep(10000); // Simular procesamiento
            ack.acknowledge(); // Confirmar el procesamiento exitoso del mensaje
            System.out.println("Acknowledged Recibido: " + notificacion.toString());
            
        } catch (Exception e) {
           System.out.println(" Errror al procesar el mensaje: " + e.getMessage());
           e.printStackTrace();
        }
     }


    
}
