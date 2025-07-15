package cl.duoc.azuread.ejemplo.service;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import cl.duoc.azuread.ejemplo.config.KafkaConsumerConfig;
import cl.duoc.azuread.ejemplo.dto.NotificacionDTO;

@Service
public class KafkaProducerServiceImpl implements KafkaProducerService {


    @Autowired
    private KafkaTemplate<String, NotificacionDTO> kafkaTemplate;
    
    @Override
    public void enviarNotificacion(NotificacionDTO notificacion) {

        notificacion.setId(System.nanoTime());
        notificacion.setFecha(Instant.now().toString());

        kafkaTemplate.send(KafkaConsumerConfig.TOPIC, notificacion);
   
    }


    
}
