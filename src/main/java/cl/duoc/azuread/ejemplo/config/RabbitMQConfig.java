package cl.duoc.azuread.ejemplo.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class RabbitMQConfig {

    public static final String MAIN_QUEUE = "mensajes.colas2";
    public static final String DLX_EXCHANGE = "dlx-exchange";
    public static final String DLX_QUEUE = "dlx-queue";
    public static final String DLX_ROUTING_KEY = "dlx-routing-key";
    public static final String COLA_VENTAS = "ventas";
    public static final String COLA_PROMOCIONES = "cola.promociones";

    @Bean(name = "colaVentas") 
    public Queue colaVentas() {
        return new Queue(COLA_VENTAS, true);
    }

    @Bean(name = "colaPromociones") 
    public Queue colaPromociones() {
        return new Queue(COLA_PROMOCIONES, true);
    }

    @Bean(name = "mainQueue") 
    public Queue mainQueue() {
        return new Queue(MAIN_QUEUE, true, false, false, Map.of(
            "x-dead-letter-exchange", DLX_EXCHANGE,
            "x-dead-letter-routing-key", DLX_ROUTING_KEY,
            "x-max-length", 5
        ));
    }

    @Bean
    public DirectExchange dlxExchange() {
        return new DirectExchange(DLX_EXCHANGE);
    }

    @Bean
    public Queue dlxQueue() {
        return new Queue(DLX_QUEUE);
    }

    @Bean
    public Binding dlxBinding() {
        return BindingBuilder
                .bind(dlxQueue())
                .to(dlxExchange())
                .with(DLX_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public CachingConnectionFactory connectionFactory() {
        CachingConnectionFactory factory = new CachingConnectionFactory("rabbitmq1");
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setPort(5672);
        return factory;
    }

    @Bean
    public AmqpAdmin amqpAdmin(CachingConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(CachingConnectionFactory connectionFactory,
                                        Jackson2JsonMessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }

}
