package cl.duoc.azuread.ejemplo.service;

import cl.duoc.azuread.ejemplo.dto.NotificacionDTO;

public interface KafkaProducerService {

     void enviarNotificacion(NotificacionDTO notificacion);

    
    
}
