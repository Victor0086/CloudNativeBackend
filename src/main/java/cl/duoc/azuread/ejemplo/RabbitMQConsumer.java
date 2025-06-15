package cl.duoc.azuread.ejemplo;


import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import cl.duoc.azuread.ejemplo.service.MensajeService;
import jakarta.annotation.PostConstruct;


@Component
public class RabbitMQConsumer {

    @Autowired
    private MensajeService mensajeService;
    @PostConstruct
    public void consumirCola() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        System.out.println(">>> Iniciando consumidor RabbitMQ...");
        factory.setHost("rabbitmq");
        factory.setUsername("guest");
        factory.setPassword("guest");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // Declarar el exchange y la cola
        // y vincularlos con una routing key
        String exchangeName = "myExchange";
        String routingKey = "myKey";
        String queueName = "mensajes.colas";

        channel.exchangeDeclare(exchangeName, "direct", true); // tipo: direct

        // Declarar la cola
        channel.queueDeclare(queueName, true, false, false, null);

        // Vincular la cola al exchange con la routing key
        channel.queueBind(queueName, exchangeName, routingKey);

        // Consumir mensajes normalmente
        channel.basicConsume(queueName, true, (tag, delivery) -> {
            String mensaje = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println("[x] Recibido: " + mensaje);
            mensajeService.guardarMensaje(mensaje);
        }, tag -> {});
    }

}

