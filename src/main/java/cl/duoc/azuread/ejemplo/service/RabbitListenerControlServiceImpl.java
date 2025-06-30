package cl.duoc.azuread.ejemplo.service;


import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.stereotype.Service;

@Service
public class RabbitListenerControlServiceImpl implements RabbitListenerControlService {

    private final RabbitListenerEndpointRegistry registry;

    public RabbitListenerControlServiceImpl(RabbitListenerEndpointRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void pausarListener(String id) {
        MessageListenerContainer container = registry.getListenerContainer(id);
        if (container != null && container.isRunning()) {
            container.stop();
            System.out.println("Listener pausado: " + id);
        }
    }

    @Override
    public void reanudarListener(String id) {
        MessageListenerContainer container = registry.getListenerContainer(id);
        if (container != null && !container.isRunning()) {
            container.start();
            System.out.println("Listener reanudado: " + id);
        }
    }

    @Override
    public boolean isListenerRunning(String id) {
        MessageListenerContainer container = registry.getListenerContainer(id);
        return container != null && container.isRunning();
    }
}
