package cl.duoc.azuread.ejemplo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.stereotype.Service;

@Service
public class KafkaAdminListenerServiceImpl implements KafkaAdminListenerService {


    @Autowired
    private KafkaListenerEndpointRegistry registry;

    @Override
    public void pausarListener(String id) {
  
        registry.getListenerContainer(id).pause();
    }

    @Override
    public void reanudarListener(String id) {
        registry.getListenerContainer(id).resume();
    }

    @Override
    public void stopListener(String id) {
        if (registry.getListenerContainer(id) != null) {
            registry.getListenerContainer(id).stop();
            System.out.println("Listener detenido: " + id);
        } else {
            System.out.println("No se encontró el listener con ID: " + id);
        }        
    }

    @Override
    public void statusListener(String id) {
        if (registry.getListenerContainer(id) != null) {
            boolean isRunning = registry.getListenerContainer(id).isRunning();
            System.out.println("Estado del listener " + id + ": " + (isRunning ? "Activo" : "Inactivo"));
        } else {
            System.out.println("No se encontró el listener con ID: " + id);
        }
    }

    
}
