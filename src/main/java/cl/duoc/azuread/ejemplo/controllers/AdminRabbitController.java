package cl.duoc.azuread.ejemplo.controllers;

import cl.duoc.azuread.ejemplo.dto.BindingDTO;
import cl.duoc.azuread.ejemplo.service.AdminRabbitService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/rabbit")
public class AdminRabbitController {

    private final AdminRabbitService service;

    public AdminRabbitController(AdminRabbitService service) {
        this.service = service;
    }

    @PostMapping("/colas/{nombrecola}")
    public String crearCola(@PathVariable String nombrecola) {
        service.crearCola(nombrecola);
        return "Cola creada: " + nombrecola;
    }

    @DeleteMapping("/colas/{nombrecola}")
    public String eliminarCola(@PathVariable String nombrecola) {
        service.eliminarCola(nombrecola);
        return "Cola eliminada: " + nombrecola;
    }

    @PostMapping("/exchanges/{nombreexchange}")
    public String crearExchange(@PathVariable String nombreexchange) {
        service.crearExchange(nombreexchange);
        return "Exchange creado: " + nombreexchange;
    }

    @DeleteMapping("/exchanges/{nombreexchange}")
    public String eliminarExchange(@PathVariable String nombreexchange) {
        service.eliminarExchange(nombreexchange);
        return "Exchange eliminado: " + nombreexchange;
    }

    @PostMapping("/bindings")
    public String crearBinding(@RequestBody BindingDTO request) {
        service.crearBinding(request);
        return "Binding creado entre cola: " + request.getNombreCola() + " y exchange: " + request.getNombreExchange();
    }
}
