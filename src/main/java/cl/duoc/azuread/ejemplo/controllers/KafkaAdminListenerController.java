package cl.duoc.azuread.ejemplo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import cl.duoc.azuread.ejemplo.service.KafkaAdminListenerService;


@RestController
@RequestMapping("/kafka-listener")
public class KafkaAdminListenerController {

    @Autowired
    private KafkaAdminListenerService kafkaAdminListenerService;


    @PostMapping("/{id}/pausar")
    public String pausarListener(@PathVariable String id) {
        kafkaAdminListenerService.pausarListener(id);

        return "Listener " + id + " pausado correctamente.";
    }

    @PostMapping("/{id}/reanudar")
    public String reanudarListener(@PathVariable String id) {
        kafkaAdminListenerService.reanudarListener(id);

        return "Listener " + id + " reanudado correctamente.";
    }

    @PostMapping("/{id}/detener")
    public String detenerListener(@PathVariable String id) {
        kafkaAdminListenerService.stopListener(id);

        return "Listener " + id + " detenido correctamente.";
    }

    @GetMapping("/{id}/estado")
    public String estadoListener (@PathVariable String id) {
        kafkaAdminListenerService.statusListener(id);
        
        return "Consulta realizada para el listener " + id + ". Revisa los logs para ver el estado.";   
    }




    
}
