package cl.duoc.azuread.ejemplo.service;



import org.springframework.amqp.core.*;
import org.springframework.stereotype.Service;
import cl.duoc.azuread.ejemplo.dto.BindingDTO;

@Service
public class AdminRabbitServiceImpl implements AdminRabbitService {

    private final AmqpAdmin amqpAdmin;

    public AdminRabbitServiceImpl(AmqpAdmin amqpAdmin) {
        this.amqpAdmin = amqpAdmin;
    }

    @Override
    public void crearCola(String nombreCola) {
        Queue queue = new Queue(nombreCola, true);
        amqpAdmin.declareQueue(queue);
    }

    @Override
    public void crearExchange(String nombreExchange) {
        DirectExchange exchange = new DirectExchange(nombreExchange, true, false);
        amqpAdmin.declareExchange(exchange);
    }

    @Override
    public void crearBinding(BindingDTO request) {
        Binding binding = BindingBuilder
                .bind(new Queue(request.getNombreCola()))
                .to(new DirectExchange(request.getNombreExchange()))
                .with(request.getRoutingKey());

        amqpAdmin.declareBinding(binding);
    }

    @Override
    public void eliminarCola(String nombreCola) {
        amqpAdmin.deleteQueue(nombreCola);
    }

    @Override
    public void eliminarExchange(String nombreExchange) {
        amqpAdmin.deleteExchange(nombreExchange);
    }
}
