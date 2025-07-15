package cl.duoc.azuread.ejemplo.service;

import org.springframework.kafka.support.Acknowledgment;
import cl.duoc.azuread.ejemplo.dto.NotificacionDTO;

public interface KafkaConsumerService {
    
    void consumirNotificaciones(NotificacionDTO notificacion, Acknowledgment ack);
}
