package cl.duoc.azuread.ejemplo.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;

import cl.duoc.azuread.ejemplo.dto.NotificacionDTO;
import cl.duoc.azuread.ejemplo.dto.VentaKafkaDTO;
import cl.duoc.azuread.ejemplo.dto.StockUpdateDTO;
import cl.duoc.azuread.ejemplo.dto.PromocionKafkaDTO;


import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {
    
    // TÓPICOS PARA MICROSERVICIOS
    public static final String TOPIC_VENTAS = "ventas";
    public static final String TOPIC_STOCK = "stock";
    public static final String TOPIC_PROMOCIONES = "promociones";
    
    // TÓPICO ORIGINAL
    public static final String TOPIC = "notificaciones";
    public static final String CONSUMER_GROUP_ID = "consumidores";
    // CONFIGURACIÓN DINÁMICA: localhost para local, nombres de servicio para Docker
    public static final String BOOTSTRAP_SERVERS = System.getProperty("kafka.bootstrap.servers", "localhost:9092,localhost:9093,localhost:9094");

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

        return new NewTopic(TOPIC , 3, (short) 3);
                
    }
    
    // TÓPICOS PARA MICROSERVICIOS
    @Bean
    NewTopic ventasTopic() {
        return new NewTopic(TOPIC_VENTAS, 3, (short) 3);
    }
    
    @Bean 
    NewTopic stockTopic() {
        return new NewTopic(TOPIC_STOCK, 3, (short) 3);
    }
    
    @Bean
    NewTopic promocionesTopic() {
        return new NewTopic(TOPIC_PROMOCIONES, 3, (short) 3);
    }
    
    @Bean
    public ConsumerFactory<String, NotificacionDTO> consumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, CONSUMER_GROUP_ID);
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        
        return new DefaultKafkaConsumerFactory<>(configProps, new StringDeserializer(), 
                new JsonDeserializer<>(NotificacionDTO.class, false));
    }
    
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, NotificacionDTO> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, NotificacionDTO> factory = 
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return factory;
    }
    
    // Configuración específica para VentaKafkaDTO
    @Bean
    public ConsumerFactory<String, VentaKafkaDTO> ventaKafkaConsumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "inventario-group");
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        configProps.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class.getName());
        configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        configProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, VentaKafkaDTO.class.getName());
        
        return new DefaultKafkaConsumerFactory<>(configProps, new StringDeserializer(), 
                new ErrorHandlingDeserializer<>(new JsonDeserializer<>(VentaKafkaDTO.class, false)));
    }
    
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, VentaKafkaDTO> ventaKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, VentaKafkaDTO> factory = 
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(ventaKafkaConsumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return factory;
    }
    
    // Configuración específica para StockUpdateDTO
    @Bean
    public ConsumerFactory<String, StockUpdateDTO> stockUpdateConsumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "promociones-group");
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        configProps.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class.getName());
        configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        configProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, StockUpdateDTO.class.getName());
        
        return new DefaultKafkaConsumerFactory<>(configProps, new StringDeserializer(), 
                new ErrorHandlingDeserializer<>(new JsonDeserializer<>(StockUpdateDTO.class, false)));
    }
    
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, StockUpdateDTO> stockUpdateListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, StockUpdateDTO> factory = 
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(stockUpdateConsumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return factory;
    }
    
    // Configuración específica para PromocionKafkaDTO
    @Bean
    public ConsumerFactory<String, PromocionKafkaDTO> promocionKafkaConsumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "notificaciones-group");
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        configProps.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class.getName());
        configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        configProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, PromocionKafkaDTO.class.getName());
        
        return new DefaultKafkaConsumerFactory<>(configProps, new StringDeserializer(), 
                new ErrorHandlingDeserializer<>(new JsonDeserializer<>(PromocionKafkaDTO.class, false)));
    }
    
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PromocionKafkaDTO> promocionKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, PromocionKafkaDTO> factory = 
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(promocionKafkaConsumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return factory;
    }


}
