package cl.duoc.azuread.ejemplo.service;

import cl.duoc.azuread.ejemplo.config.RabbitMQConfig;
import cl.duoc.azuread.ejemplo.model.Mensaje;
import cl.duoc.azuread.ejemplo.model.MensajeError;
import cl.duoc.azuread.ejemplo.repository.MensajeErrorRepository;
import cl.duoc.azuread.ejemplo.repository.MensajeRepository;
import org.springframework.amqp.core.Message;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
public class MensajeServiceImpl implements MensajeService {

    @Autowired
    private MensajeRepository mensajeRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private MensajeErrorRepository mensajeErrorRepository;

    @Override
    public void guardarMensaje(String contenido) {
        Mensaje msg = new Mensaje();
        msg.setContenido(contenido);
        msg.setFechaRecepcion(LocalDateTime.now());
        mensajeRepository.save(msg);
        System.out.println("[✓] Guardado en Oracle: " + contenido);
    }

    @Override
    public void guardarMensajeError(String contenido) {
        MensajeError error = new MensajeError();
        error.setContenido(contenido);
        error.setFechaError(LocalDateTime.now());
        mensajeErrorRepository.save(error);
        System.out.println("[✗] Guardado en tabla mensaje_error: " + contenido);
    }

    @Override
    public void enviarMensaje(String mensaje) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.MAIN_QUEUE, mensaje);
        System.out.println("[→] Enviado a cola principal: " + mensaje);
    }

    @RabbitListener(id = "listener-myQueue", queues =  "mensajes.colas2", ackMode = "MANUAL")
    public void recibirMensajeConAckManual(Message mensaje, Channel canal) throws IOException {
        try {
            System.out.println("[✓] Mensaje recibido (manual): " + new String(mensaje.getBody()));

            // Pausa para que el mensaje sea visible como "unacked" en RabbitMQ
            Thread.sleep(10000); // 10 segundos

            // Procesar el mensaje normalmente
            guardarMensaje(new String(mensaje.getBody()));

            // Confirmar recepción del mensaje
            canal.basicAck(mensaje.getMessageProperties().getDeliveryTag(), false);
            System.out.println("[✓] Acknowledge OK enviado");
        } catch (Exception e) {
            // Rechazar el mensaje (por ejemplo, lo envía a DLQ si está configurado)
            canal.basicNack(mensaje.getMessageProperties().getDeliveryTag(), false, false);
            System.out.println("[✗] Acknowledge NO OK enviado");
        }
    }

        

}
