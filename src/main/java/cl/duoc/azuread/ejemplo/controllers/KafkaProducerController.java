package cl.duoc.azuread.ejemplo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.azuread.ejemplo.dto.NotificacionDTO;
import cl.duoc.azuread.ejemplo.service.KafkaProducerService;


@RestController
@RequestMapping
public class KafkaProducerController {

    @Autowired

    private KafkaProducerService kafkaProducerService;

    @PostMapping("/notificaciones")
    public String crearNotificaciones(@RequestBody() NotificacionDTO request) {
        kafkaProducerService.enviarNotificacion(request);
        return "Notificación enviada correctamente" + request.toString();
    }
}
