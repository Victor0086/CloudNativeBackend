package cl.duoc.azuread.ejemplo.listener;

import cl.duoc.azuread.ejemplo.model.MensajeError;
import cl.duoc.azuread.ejemplo.repository.MensajeErrorRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DeadLetterConsumer {

    private final MensajeErrorRepository mensajeErrorRepository;

    public DeadLetterConsumer(MensajeErrorRepository mensajeErrorRepository) {
        this.mensajeErrorRepository = mensajeErrorRepository;
    }

    @RabbitListener(queues = "dlx-queue")
    public void recibirMensajeFallido(String mensaje) {
        System.out.println("Recibido en carta muerta: " + mensaje);

        MensajeError error = new MensajeError();
        error.setContenido(mensaje);
        error.setFechaError(LocalDateTime.now());

        mensajeErrorRepository.save(error);

        System.out.println("[✓] Guardado en tabla mensaje_error: " + mensaje);
    }
}
