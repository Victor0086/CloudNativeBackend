package cl.duoc.azuread.ejemplo.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import cl.duoc.azuread.ejemplo.model.Venta;
import cl.duoc.azuread.ejemplo.service.VentaRabbitService;

@RestController
@RequestMapping("/ventas-rabbit")
public class VentaRabbitController {

    private final VentaRabbitService ventaRabbitService;

    public VentaRabbitController(VentaRabbitService ventaRabbitService) {
        this.ventaRabbitService = ventaRabbitService;
    }

    @PostMapping
    public ResponseEntity<String> registrarVentaRabbit(@RequestBody Venta venta) {
        ventaRabbitService.enviarVenta(venta);
        return ResponseEntity.ok("Venta enviada a RabbitMQ\nID Venta: " + venta.getId());
    }
}