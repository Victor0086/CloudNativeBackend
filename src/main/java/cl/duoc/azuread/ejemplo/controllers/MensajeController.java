package cl.duoc.azuread.ejemplo.controllers;

import cl.duoc.azuread.ejemplo.service.MensajeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mensajes")
public class MensajeController {

    @Autowired
    private MensajeService mensajeService;

    @PostMapping ("/enviar")
    public String enviarMensaje(@RequestBody String contenido) {
        mensajeService.enviarMensaje(contenido);
        return "Mensaje enviado: " + contenido;
    }
}
