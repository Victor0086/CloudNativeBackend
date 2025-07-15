package cl.duoc.azuread.ejemplo.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import cl.duoc.azuread.ejemplo.dto.NotificacionDTO;

public class KafkaProducerConfig {


    // CONFIGURACIÓN DINÁMICA: localhost para local, nombres de servicio para Docker
    public static final String BOOTSTRAP_SERVERS = System.getProperty("kafka.bootstrap.servers", "localhost:9092,localhost:9093,localhost:9094");
    private static final String TOPIC = "notificaciones";

    @Bean
    KafkaAdmin kafkaAdmin() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        return new KafkaAdmin(configProps);
    }



    @Bean
    NewTopic notificacionesTopic() {
        
        Map<String, Object> configs = new HashMap<>();
        configs.put("retention.ms", 43200000);

        return new NewTopic(TOPIC , 3, (short) 2);
                
    }

    @Bean
    ProducerFactory<String, NotificacionDTO> producerFactory() {

        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    KafkaTemplate<String, NotificacionDTO> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
    


}
